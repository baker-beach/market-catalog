package com.bakerbeach.market.catalog.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import com.bakerbeach.market.catalog.model.AbstractProduct;
import com.bakerbeach.market.catalog.model.AssetGroupImpl;
import com.bakerbeach.market.catalog.model.AssetImpl;
import com.bakerbeach.market.catalog.model.AssetsImpl;
import com.bakerbeach.market.catalog.model.CategoryFilter;
import com.bakerbeach.market.catalog.model.FieldFilter;
import com.bakerbeach.market.catalog.model.FieldOption;
import com.bakerbeach.market.catalog.model.GroupedProduct;
import com.bakerbeach.market.catalog.model.GroupedProductImpl;
import com.bakerbeach.market.catalog.model.PriceRangeFilter;
import com.bakerbeach.market.catalog.model.SimpleProductImpl;
import com.bakerbeach.market.core.api.model.Asset;
import com.bakerbeach.market.core.api.model.AssetGroup;
import com.bakerbeach.market.core.api.model.Assets;
import com.bakerbeach.market.core.api.model.Filter;
import com.bakerbeach.market.core.api.model.FilterList;
import com.bakerbeach.market.core.api.model.Option;
import com.bakerbeach.market.core.api.model.Product;

public class SolrProductDaoLiveImpl implements SolrProductDao {
	private static final List<String> DEFAULT_LOGO_KEYS = Arrays.asList("technical");
	private static final List<String> DEFAULT_TAG_KEYS = Arrays.asList("collection", "look");
	private static final Integer DEFAULT_LIMIT = 120;

	private String url;
	private List<String> logoKeys = DEFAULT_LOGO_KEYS;
	private List<String> tagKeys = DEFAULT_TAG_KEYS;

	@Override
	public List<GroupedProduct> groupQuery(Locale locale, String priceGroup, Currency currency, FilterList filterList,
			String query, String groupBy, Integer pageSize, Integer currentPage, String sort) {
		try {
			SolrServer solr = new HttpSolrServer(url);
			
			SolrQuery q = new SolrQuery();
			q.setQuery(query);
			q.setParam("group", true);
			q.setParam("rows", (pageSize != null) ? pageSize.toString() : DEFAULT_LIMIT.toString());
			q.setParam("group.field", groupBy);
			q.setParam("group.limit", "100");
			q.setParam("group.ngroups", true);
			// q.setParam("group.sort", "s_detailsort asc");
			if (currentPage != null) {
				Integer offset = ((currentPage - 1) * pageSize);
				q.setParam("start", offset.toString());
			}
			if (sort != null && !sort.isEmpty()) {
				q.setParam("sort", sort);
			}
			q.setFilterQueries("active_from:[* TO NOW] AND active_to:[NOW TO *]");

			// facet search ---
			if (filterList != null) {
				q.setFacet(true);
				q.setFacetSort("index");
				q.setFacetMinCount(1);
				q.setFacetLimit(-1);
				setFacetFilter(q, filterList);
			}

			QueryResponse rsp = solr.query(q);
			List<GroupedProduct> groupedProducts = getGroupedProducts(rsp, locale, priceGroup,
					currency.getCurrencyCode(), groupBy);
			
			updateFilterList(rsp, filterList);				


			return groupedProducts;
		} catch (Exception e) {
			// LOG.error(ExceptionUtils.getStackTrace(e));
			return new ArrayList<GroupedProduct>();
		}
	}

	private List<GroupedProduct> getGroupedProducts(QueryResponse rsp, Locale locale, String priceGroup,
			String currencyCode, String groupBy) {
		GroupResponse groupResponse = rsp.getGroupResponse();
		List<GroupedProduct> groupedProducts = new ArrayList<GroupedProduct>();

		List<GroupCommand> groupCommands = groupResponse.getValues();
		if (groupCommands != null && groupCommands.size() > 0) {
			GroupCommand groupCommand = groupResponse.getValues().get(0);
			List<Group> groups = groupCommand.getValues();

			for (Group group : groups) {
				List<Product> members = new ArrayList<Product>();
				for (SolrDocument doc : group.getResult()) {
					Product product = createProduct(doc, locale, priceGroup, currencyCode);
					members.add(product);
				}
				GroupedProductImpl groupedProduct = new GroupedProductImpl(group.getGroupValue());
				groupedProduct.setMembers(members);

				if (!group.getResult().isEmpty()) {
					SolrDocument doc = group.getResult().get(0);
					if (doc.containsKey(groupBy.concat("_dim_1"))) {
						groupedProduct.setDim1((String) doc.get(groupBy.concat("_dim_1"))); 
					}					
					if (doc.containsKey(groupBy.concat("_dim_2"))) {
						groupedProduct.setDim2((String) doc.get(groupBy.concat("_dim_2"))); 
					}					
				}
				
				// just get some values from first member to be represented in the grouped product.
				if (members.size() > 0) {
					groupedProduct.setMainCategory(members.get(0).getMainCategory());
					groupedProduct.setCategories(members.get(0).getCategories());
				}

				groupedProducts.add(groupedProduct);
			}
		}

		return groupedProducts;
	}

	@SuppressWarnings("unchecked")
	private Product createProduct(SolrDocument doc, Locale locale, String priceGroup, String currencyCode) {
		AbstractProduct product;
		// TODO: switch between product types
		product = new SimpleProductImpl();

		product.setGtin((String) doc.getFieldValue("gtin"));

		if (doc.containsKey("primary_group")) {
			product.setPrimaryGroup((String) doc.getFieldValue("primary_group"));
		}

		if (doc.containsKey("secondary_group")) {
			product.setSecondaryGroup((String) doc.getFieldValue("secondary_group"));
		}
				
		product.setBrand((String) doc.getFieldValue("brand_code"));
		product.setSize((String) doc.getFieldValue("size_code"));
		product.setColor((String) doc.getFieldValue("color_code"));

		// prices ---
		String priceKey = new StringBuilder(currencyCode).append("_").append(priceGroup).append("_price").toString()
				.toLowerCase();
		product.setPrice(BigDecimal.valueOf((float) doc.getFieldValue(priceKey)));

		String stdPriceKey = new StringBuilder(currencyCode).append("_").append(priceGroup).append("_std_price")
				.toString().toLowerCase();
		product.setStdPrice(BigDecimal.valueOf((float) doc.getFieldValue(stdPriceKey)));

		// max orderable quantity ---
		String moqKey = new StringBuilder(priceGroup).append("_moq").toString().toLowerCase();
		if (doc.containsKey(moqKey)) {
			product.setMoq((Integer) doc.getFieldValue(moqKey));
		} else {
			product.setMoq((Integer) doc.getFieldValue("default_moq"));
		}

		for (String logoKey : logoKeys) {
			String key = new StringBuilder("logos_").append(logoKey).append("_codes").toString().toLowerCase();
			if (doc.getFieldValue(key) != null)
				product.getLogos().put(logoKey, (List<String>) doc.getFieldValue(key));
		}

		for (String tagKey : tagKeys) {
			String key = new StringBuilder("tags_").append(tagKey).append("_codes").toString().toLowerCase();
			if (doc.getFieldValue(key) != null)
				product.getTags().put(tagKey, (List<String>) doc.getFieldValue(key));
		}

		if (doc.containsKey("listing_m_0_asset_path")) {
			Assets assets = new AssetsImpl();
			AssetGroup assetGroup = new AssetGroupImpl();
			Asset asset = new AssetImpl();
			asset.setPath((String) doc.get("listing_m_0_asset_path"));
			assetGroup.put(AssetGroupImpl.SIZE_MEDIUM, asset);
			assets.add("listing", assetGroup);
			product.setAssets(assets);
		}

		if (doc.containsKey("category_codes")) {
			product.setCategories((List<String>) doc.get("category_codes"));
		}

		if (doc.containsKey("variant1")) {
			product.setVariant1((String) doc.get("variant1"));
		}

		if (doc.containsKey("variant1Sort")) {
			product.setVariant1Sort((String) doc.get("variant1Sort"));
		}

		if (doc.containsKey("variant2")) {
			product.setVariant2((String) doc.get("variant2"));
		}

		if (doc.containsKey("variant2Sort")) {
			product.setVariant2Sort((String) doc.get("variant2Sort"));
		}

		return product;
	}

	private void setFacetFilter(SolrQuery query, FilterList filterList) {
		if (filterList != null) {
			for (Filter filter : filterList.getAvailable()) {
				if (filter instanceof CategoryFilter)
					if (filter.isActive()) {
						StringBuilder fq = new StringBuilder();
						fq.append(String.format("{!tag=%s,%s}", filter.getIndexFieldName(), filter.getIndexFieldName()));
//						fq.append(String.format("{!tag=all,%s}", filter.getIndexFieldName()));
						for (Option option : filter.getSelectedOptions()) {
							fq.append(filter.getIndexFieldName()).append(":\"").append(option.getCode()).append("\"").append(" OR ");
							
							if (fq.toString().endsWith(" OR ")) {
								fq.delete(fq.length() - 4, fq.length());
							}
						}
						query.addFilterQuery(fq.toString());
						query.addFacetField(
								String.format("{!ex=%s}%s", filter.getIndexFieldName(), filter.getIndexFieldName()));
//								String.format("{!ex=all}%s", filter.getIndexFieldName(), filter.getIndexFieldName()));
					} else {
						query.addFacetField(filter.getIndexFieldName());
					}
				else if (filter instanceof FieldFilter) {
					if (filter.isActive()) {
						StringBuilder fq = new StringBuilder();
						fq.append(String.format("{!tag=%s,%s}", filter.getIndexFieldName(), filter.getIndexFieldName()));
//						fq.append(String.format("{!tag=all,%s}", filter.getIndexFieldName()));

						for (Option option : filter.getSelectedOptions()) {
							fq.append(filter.getIndexFieldName()).append(":\"").append(option.getCode()).append("\"").append(" OR ");
						}

						if (fq.toString().endsWith(" OR ")) {
							fq.delete(fq.length() - 4, fq.length());
						}

						query.addFilterQuery(fq.toString());
						query.addFacetField(
//								String.format("{!ex=all}%s", filter.getIndexFieldName()));
								String.format("{!ex=%s}%s", filter.getIndexFieldName(), filter.getIndexFieldName()));
					} else {
						query.addFacetField(filter.getIndexFieldName());
					}
				} else if (filter instanceof PriceRangeFilter) {
					query.setGetFieldStatistics(true);
					query.setGetFieldStatistics(filter.getIndexFieldName());
					query.addStatsFieldFacets(filter.getIndexFieldName());
					query.addFacetField(
							String.format("{!ex=%s}%s", filter.getIndexFieldName(), filter.getIndexFieldName()));
//					String.format("{!ex=all}%s", filter.getIndexFieldName()));

					if (filter.isActive()) {
						String min = "*";
						String max = "*";
						StringBuilder fq = new StringBuilder();
						fq.append(String.format("{!tag=%s,%s}", filter.getIndexFieldName(), filter.getIndexFieldName()));
//						fq.append(String.format("{!tag=all,%s}", filter.getIndexFieldName()));

						for (Option option : filter.getSelectedOptions()) {
							if (option.getCode().equals("min_price")) {
								min = option.getValue();
							}

							if (option.getCode().equals("max_price")) {
								max = option.getValue();
							}

							if (option.getCode().equals("sale") && option.getValue().equals("true")) {
								String priceType = filter.getIndexFieldName().substring(0,
										filter.getIndexFieldName().indexOf("_"));
								query.addFilterQuery(String.format("%s_has_reduced_price:true", priceType));
							}
						}
						if (min != "*" || max != "*") {
							fq.append(String.format("%s:[%s TO %s]", filter.getIndexFieldName(), min, max));
							query.addFilterQuery(fq.toString());
						}
						String initialFilterId = String.format("initial_%s", filter.getIndexFieldName());
						query.setGetFieldStatistics(String.format("{!ex=all key=%s}%s", filter.getIndexFieldName(),
								initialFilterId, filter.getIndexFieldName()));
					}
				}
			}
		}
	}

	private void updateFilterList(QueryResponse rsp, FilterList filterList) {
		if (filterList != null) {
			List<FacetField> fields = rsp.getFacetFields();
			if (fields != null) {
				for (FacetField facetField : fields) {
					String id = facetField.getName();
					if (filterList.containsId(id)) {
						Filter filter = filterList.get(id);
						if (filter instanceof FieldFilter || filter instanceof CategoryFilter) {
							ArrayList<String> selectedOptions = new ArrayList<String>();
							for (Option o : filter.getSelectedOptions()) {
								selectedOptions.add(o.getCode());
							}
							filter.getOptions().clear();
							
							for (Count count : facetField.getValues()) {
								String v = count.getName();
								v = v.replaceFirst("^.*\\|", "");
								Long c = count.getCount();
								Option option;
								if (selectedOptions.contains(v))
									option = new FieldOption(v, c, true);
								else
									option = new FieldOption(v, c, false);
								filter.addOption(option);
							}
						} else if (filter instanceof PriceRangeFilter) {
							PriceRangeFilter f = (PriceRangeFilter) filter;
							Map<String, FieldStatsInfo> statsInfo = rsp.getFieldStatsInfo();
							if (statsInfo != null) {
								FieldStatsInfo info = statsInfo.get(f.getId());
								String initialFilterId = String.format("initial_%s", f.getId());
								FieldStatsInfo initialInfo = statsInfo.get(initialFilterId);
								if (info != null) {
									if (initialInfo != null) {
										f.setMin((int) Math.floor((Double) initialInfo.getMin()));
										f.setMax((int) Math.ceil((Double) initialInfo.getMax()));
									} else {
										f.setMin((int) Math.floor((info.getMin() != null)? (Double) info.getMin() : 0.d));
										f.setMax((int) Math.ceil((info.getMax() != null)? (Double) info.getMax() : 0.d));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

}