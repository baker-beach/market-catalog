package com.bakerbeach.market.xcatalog.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.xcatalog.model.Asset;
import com.bakerbeach.market.xcatalog.model.AssetImpl;
import com.bakerbeach.market.xcatalog.model.CategoryFacetImpl;
import com.bakerbeach.market.xcatalog.model.Facet;
import com.bakerbeach.market.xcatalog.model.FacetOption;
import com.bakerbeach.market.xcatalog.model.FacetOptionImpl;
import com.bakerbeach.market.xcatalog.model.Facets;
import com.bakerbeach.market.xcatalog.model.FieldFacetImpl;
import com.bakerbeach.market.xcatalog.model.Group;
import com.bakerbeach.market.xcatalog.model.PriceImpl;
import com.bakerbeach.market.xcatalog.model.PriceRangeFacetImpl;
import com.bakerbeach.market.xcatalog.model.Product;
import com.bakerbeach.market.xcatalog.model.ProductImpl;

public abstract class AbstractSolrProductDao<G extends Group, P extends Product> implements SolrProductDao {
	protected static final Logger log = LoggerFactory.getLogger(AbstractSolrProductDao.class);

	private static final Integer DEFAULT_LIMIT = 120;

	private String url;
	protected ObjectMapper mapper = new ObjectMapper();

	protected Class<P> productClass;
	protected Class<G> groupClass;

	public AbstractSolrProductDao(Class<G> groupClass, Class<P> productClass, String url) {
		this.groupClass = groupClass;
		this.productClass = productClass;
		this.url = url;
	}

	@Override
	public GroupResult groupQuery(Locale locale, String priceGroup, Currency currency, Facets facets, String query,
			List<String> filterQueries, String groupBy, Integer pageSize, Integer currentPage, String sort) {
		try {
			SolrServer solr = new HttpSolrServer(url);

			SolrQuery q = new SolrQuery();
			q.setQuery(query);
			q.setParam("group", true);
			q.setParam("rows", (pageSize != null) ? pageSize.toString() : DEFAULT_LIMIT.toString());
			q.setParam("group.field", groupBy);
			q.setParam("group.limit", "100");
			q.setParam("group.ngroups", true);
			// TODO: group.sort (e.g. primary_group_sort)
			if (currentPage != null) {
				Integer offset = ((currentPage - 1) * pageSize);
				q.setParam("start", offset.toString());
			}
			if (sort != null && !sort.isEmpty()) {
				q.setParam("sort", sort);
			}
			q.setFilterQueries("active_from:[* TO NOW] AND active_to:[NOW TO *]");

			// facets ---
			if (facets != null) {
				q.setFacet(true);
				q.setFacetSort("index");
				q.setFacetMinCount(1);
				q.setFacetLimit(-1);
				setFacetFilter(q, facets);
			}

			if (CollectionUtils.isNotEmpty(filterQueries)) {
				filterQueries.forEach(fq -> q.addFilterQuery(fq));
			}

			QueryResponse rsp = solr.query(q);
			List<Group> groups = getGroupedProducts(rsp, locale, priceGroup, currency.getCurrencyCode(),
					groupBy);

			Integer nGroups = null;

			GroupResponse groupResponse = rsp.getGroupResponse();
			List<GroupCommand> groupCommands = groupResponse.getValues();
			if (CollectionUtils.isNotEmpty(groupCommands)) {
				GroupCommand groupCommand = groupResponse.getValues().get(0);
				nGroups = groupCommand.getNGroups();
			}

			updateFilterList(rsp, facets);

			GroupResult result = new GroupResult();
			result.setGroups(groups);
			result.setNGroups(nGroups);

			return result;
		} catch (SolrServerException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	protected List<Group> getGroupedProducts(QueryResponse rsp, Locale locale, String priceGroup, String currencyCode, String groupBy) {
		GroupResponse groupResponse = rsp.getGroupResponse();
		List<Group> groupedProducts = new ArrayList<>();
		try {
			List<GroupCommand> groupCommands = groupResponse.getValues();
			if (groupCommands != null && groupCommands.size() > 0) {
				GroupCommand groupCommand = groupResponse.getValues().get(0);
				List<org.apache.solr.client.solrj.response.Group> groups = groupCommand.getValues();

				for (org.apache.solr.client.solrj.response.Group group : groups) {
					List<Product> members = new ArrayList<Product>();
					for (SolrDocument doc : group.getResult()) {
						Product product = createProduct(doc, locale, priceGroup, currencyCode);
						members.add(product);
					}
					Group groupedProduct = groupClass.newInstance();
					groupedProduct.setCode(group.getGroupValue());
					groupedProduct.getMembers().addAll(members);

					if (!group.getResult().isEmpty()) {
						SolrDocument doc = group.getResult().get(0);
						if (doc.containsKey(groupBy.concat("_dim_1"))) {
							groupedProduct.setDim1((String) doc.get(groupBy.concat("_dim_1")));
						}
						if (doc.containsKey(groupBy.concat("_dim_2"))) {
							groupedProduct.setDim2((String) doc.get(groupBy.concat("_dim_2")));
						}
					}

					// // just get some values from first member to be
					// represented
					// in the grouped product.
					// if (members.size() > 0) {
					// groupedProduct.setMainCategory(members.get(0).getMainCategory());
					// groupedProduct.setCategories(members.get(0).getCategories());
					// }

					groupedProducts.add(groupedProduct);
				}
			}
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		return groupedProducts;
	}

	@SuppressWarnings("unchecked")
	protected Product createProduct(SolrDocument doc, Locale locale,
			String priceGroup, String currencyCode) {
		try {
			Product product = productClass.newInstance();
			if (product instanceof ProductImpl) {
				ProductImpl productImpl = (ProductImpl) product;

				Date activeFrom = (Date) doc.get("active_from");

				Map<String, PriceImpl> priceMap = new HashMap<>();
				Map<String, List<String>> tags = productImpl.getTags();
				Map<String, List<String>> logos = productImpl.getLogos();
				Map<String, List<Map<String, Asset>>> assets = new HashMap<>();

				// TODO: listing and variants
				doc.forEach(e -> {
					if (e.getKey().equals("code")) {
						productImpl.setCode((String) e.getValue());
					} else if (e.getKey().equals("type")) {
						productImpl.setType(Product.Type.valueOf((String) e.getValue()));
					} else if (e.getKey().equals("unit_code")) {
						productImpl.setUnit(Product.Unit.valueOf((String) e.getValue()));
					} else if (e.getKey().equals("gtin")) {
						productImpl.setGtin((String) e.getValue());
					} else if (e.getKey().equals("primary_group")) {
						productImpl.setPrimaryGroup((String) e.getValue());
					} else if (e.getKey().equals("secondary_group")) {
						productImpl.setSecondaryGroup((String) e.getValue());
					} else if (e.getKey().equals("brand_code")) {
						productImpl.setBrand((String) e.getValue());
					} else if (e.getKey().equals("size_code")) {
						productImpl.setSizeCode((String) e.getValue());
					} else if (e.getKey().equals("color_code")) {
						productImpl.setColorCode((String) e.getValue());
					} else if (e.getKey().startsWith("tags_")) {
						String[] parts = e.getKey().split("_");
						if (!tags.containsKey(parts[0])) {
							tags.put(parts[0], new ArrayList<String>());
						}
						tags.get(parts[0]).addAll((List<String>) e.getValue());
					} else if (e.getKey().startsWith("logos_")) {
						String[] parts = e.getKey().split("_");
						if (!logos.containsKey(parts[0])) {
							logos.put(parts[0], new ArrayList<String>());
						}
						logos.get(parts[0]).addAll((List<String>) e.getValue());
					} else if (e.getKey().endsWith("_price")) {
						try {
							String[] parts = e.getKey().split("_");
							
							StringBuilder keyBuilder = new StringBuilder(parts[0]).append(parts[1]);
							if (parts.length == 3) {
								keyBuilder.append("std");
							} else if (parts.length == 4) {
								keyBuilder.append(parts[2]);
							}
							String key = keyBuilder.toString();
							
							if (!priceMap.containsKey(key)) {
								PriceImpl price = new PriceImpl();
								price.setStart(activeFrom);
								price.setCurrency(Currency.getInstance(parts[0].toUpperCase()));
								price.setGroup(parts[1]);
								price.setTag(parts[2]);
								price.setValue(BigDecimal.valueOf((float) e.getValue()));
								priceMap.put(key, price);
							}
						} catch (Exception ee) {
							log.error(ExceptionUtils.getStackTrace(ee));
						}
					} else if (e.getKey().equals("base_price_1_divisor")) {
						productImpl.setBasePrice1Divisor(BigDecimal.valueOf((float) e.getValue()));
					} else if (e.getKey().equals("base_price_2_divisor")) {
						productImpl.setBasePrice2Divisor(BigDecimal.valueOf((float) e.getValue()));
					} else if (e.getKey().equals("base_price_1_unit_code")) {
						productImpl.setBasePrice1Unit((String) e.getValue());
					} else if (e.getKey().equals("base_price_2_unit_code")) {
						productImpl.setBasePrice2Unit((String) e.getValue());
					} else if (e.getKey().equals("assets")) {
						try {
							AssetMap map = mapper.readValue((String) e.getValue(), AssetMap.class);
							
							for (String tag : map.keySet()) {
								for (Map<String, AssetImpl> group : map.get(tag)) {
									productImpl.addAsset(tag, new HashMap<>(group));
								}
							}
						} catch (IOException ee) {
							log.error(ExceptionUtils.getStackTrace(ee));
						}
					}					
				});
				
				productImpl.getPrices().addAll(priceMap.values());
			}

			return product;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static class AssetMap extends LinkedHashMap<String, ArrayList<LinkedHashMap<String, AssetImpl>>> {
		private static final long serialVersionUID = 1L;
	}

	private void setFacetFilter(SolrQuery query, Facets filterList) {
		if (filterList != null) {
			for (Facet filter : filterList.getAvailable()) {
				if (filter instanceof CategoryFacetImpl)
					if (filter.isActive()) {
						StringBuilder fq = new StringBuilder();
						fq.append(
								String.format("{!tag=%s,%s}", filter.getIndexFieldName(), filter.getIndexFieldName()));
						// fq.append(String.format("{!tag=all,%s}",
						// filter.getIndexFieldName()));
						for (FacetOption option : filter.getSelectedOptions()) {
							fq.append(filter.getIndexFieldName()).append(":\"").append(option.getCode()).append("\"")
									.append(" OR ");

							if (fq.toString().endsWith(" OR ")) {
								fq.delete(fq.length() - 4, fq.length());
							}
						}
						query.addFilterQuery(fq.toString());
						query.addFacetField(
								String.format("{!ex=%s}%s", filter.getIndexFieldName(), filter.getIndexFieldName()));
						// String.format("{!ex=all}%s",
						// filter.getIndexFieldName(),
						// filter.getIndexFieldName()));
					} else {
						query.addFacetField(filter.getIndexFieldName());
					}
				else if (filter instanceof FieldFacetImpl) {
					if (filter.isActive()) {
						StringBuilder fq = new StringBuilder();
						fq.append(
								String.format("{!tag=%s,%s}", filter.getIndexFieldName(), filter.getIndexFieldName()));
						// fq.append(String.format("{!tag=all,%s}",
						// filter.getIndexFieldName()));

						for (FacetOption option : filter.getSelectedOptions()) {
							fq.append(filter.getIndexFieldName()).append(":\"").append(option.getCode()).append("\"")
									.append(" OR ");
						}

						if (fq.toString().endsWith(" OR ")) {
							fq.delete(fq.length() - 4, fq.length());
						}

						query.addFilterQuery(fq.toString());
						query.addFacetField(
								// String.format("{!ex=all}%s",
								// filter.getIndexFieldName()));
								String.format("{!ex=%s}%s", filter.getIndexFieldName(), filter.getIndexFieldName()));
					} else {
						query.addFacetField(filter.getIndexFieldName());
					}
				} else if (filter instanceof PriceRangeFacetImpl) {
					query.setGetFieldStatistics(true);
					query.setGetFieldStatistics(filter.getIndexFieldName());
					query.addStatsFieldFacets(filter.getIndexFieldName());
					query.addFacetField(
							String.format("{!ex=%s}%s", filter.getIndexFieldName(), filter.getIndexFieldName()));
					// String.format("{!ex=all}%s",
					// filter.getIndexFieldName()));

					if (filter.isActive()) {
						String min = "*";
						String max = "*";
						StringBuilder fq = new StringBuilder();
						fq.append(
								String.format("{!tag=%s,%s}", filter.getIndexFieldName(), filter.getIndexFieldName()));
						// fq.append(String.format("{!tag=all,%s}",
						// filter.getIndexFieldName()));

						for (FacetOption option : filter.getSelectedOptions()) {
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

	private void updateFilterList(QueryResponse rsp, Facets facets) {
		if (facets != null) {
			List<FacetField> fields = rsp.getFacetFields();
			if (fields != null) {
				for (FacetField facetField : fields) {
					String id = facetField.getName();
					if (facets.containsId(id)) {
						Facet filter = facets.get(id);
						if (filter instanceof FieldFacetImpl || filter instanceof CategoryFacetImpl) {
							ArrayList<String> selectedOptions = new ArrayList<String>();
							for (FacetOption o : filter.getSelectedOptions()) {
								selectedOptions.add(o.getCode());
							}
							filter.getOptions().clear();

							for (Count count : facetField.getValues()) {
								String v = count.getName();
								v = v.replaceFirst("^.*\\|", "");
								Long c = count.getCount();
								FacetOption option;
								if (selectedOptions.contains(v))
									option = new FacetOptionImpl(v, c, true);
								else
									option = new FacetOptionImpl(v, c, false);
								filter.addOption(option);
							}
						} else if (filter instanceof PriceRangeFacetImpl) {
							PriceRangeFacetImpl f = (PriceRangeFacetImpl) filter;
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
										f.setMin((int) Math
												.floor((info.getMin() != null) ? (Double) info.getMin() : 0.d));
										f.setMax((int) Math
												.ceil((info.getMax() != null) ? (Double) info.getMax() : 0.d));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static class GroupResult {
		protected List<Group> groups;
		protected Integer nGroups;

		public List<Group> getGroups() {
			return groups;
		}

		public void setGroups(List<Group> groups) {
			this.groups = groups;
		}

		public Integer getNGroups() {
			return nGroups;
		}

		public void setNGroups(Integer nGroups) {
			this.nGroups = nGroups;
		}

	}
	
	private static class AssetsWrap extends HashMap<String, List<Map<String, Asset>>> {
		
	}
}
