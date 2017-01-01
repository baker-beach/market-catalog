package com.bakerbeach.market.catalog.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bakerbeach.market.catalog.utils.FacetFilterFactory;
import com.bakerbeach.market.core.api.model.Filter;
import com.bakerbeach.market.core.api.model.FilterList;
import com.bakerbeach.market.core.api.model.Option;

public class FacetFilterList implements FilterList {
	private Map<String, Filter> available = new LinkedHashMap<String, Filter>();
	private List<String> headerOrder;

	public FacetFilterList() {
	}

	public FacetFilterList(List<String> headerOrder) {
		this.headerOrder = headerOrder;
	}
	
	@Override
	public Collection<Filter> getAvailable() {
		List<Filter> list = new ArrayList<Filter>(available.size());
		for (Filter filter : available.values()) {
//			if (filter instanceof FieldFilter || filter instanceof PriceRangeFilter) {
				list.add(filter);
//			}
		}
		return list;
//		return available.values();
	}
	
	@Override
	public void add(Filter filter) {
		available.put(filter.getId(), filter);
	}

	@Override
	public Collection<Filter> getActive() {
		return getActive(false);
	}
	
	@Override
	public Collection<Filter> getActive(Boolean url) {
		//TODO sort
		List<Filter> list = new ArrayList<Filter>(available.size());
		for (Filter filter : available.values()) {
			if (filter.isActive()) {
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Filter> getUrlRelevant() {
		List<Filter> list = new ArrayList<Filter>(available.size());
		
		for (Filter filter : available.values()) {
			if (filter.isUrlRelevant()) {
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Filter> getUrlRelevantIncluding(Filter _filter) {
		List<Filter> list = new ArrayList<Filter>(available.size());
		
		for (Filter filter : available.values()) {
			if (filter.isUrlRelevant() && (filter.isActive() || _filter != null && filter.getId().equals(_filter.getId()) && _filter.isUrlRelevant())) {
				list.add(filter);
			}
		}
		return list;
	}
	
	@Override
	public Collection<Filter> getUrlRelevantExcluding(Filter _filter) {
		List<Filter> list = new ArrayList<Filter>(available.size());

		for (Filter filter : available.values()) {
			if (filter.isUrlRelevant() && filter.isActive() && !filter.getId().equals(_filter.getId())) {
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Filter> getActiveFiltersWithGetParamtersIncluding(Filter _filter) {
		List<Filter> list = new ArrayList<Filter>(available.size());

		for (Filter filter : available.values()) {
			if (!filter.isUrlRelevant() && filter.isActive() || _filter != null && filter.getId().equals(_filter.getId()) && ! _filter.isUrlRelevant()){
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Filter> getActiveFiltersWithGetParamtersExcluding(Filter _filter) {
		List<Filter> list = new ArrayList<Filter>(available.size());

		for (Filter filter : available.values()) {
			if (!filter.isUrlRelevant() && filter.isActive() && !filter.getId().equals(_filter.getId())){
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Filter> getInactive() {
		List<Filter> list = new ArrayList<Filter>(available.size());
		for (Filter filter : available.values()) {
			if (!filter.isActive()) {
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public boolean containsId(String id) {
		id = id.replaceFirst("_codes?$", "");
		return available.containsKey(id);
	}

	@Override
	public Filter get(String id) {
		id = id.replaceFirst("_codes?$", "");
		return available.get(id);
	}

	@Override
	public void remove(String filterId) {
		available.remove(filterId);
	}

	@Override
	public List<String> getHeadlineKeywords() {
		boolean isArtFilterActive = false;
		Map<Integer, String> map = new TreeMap<Integer, String>();

		for (Filter facet : getActive()) {

			if (facet.getId().equals(FacetFilterFactory.NATURE_KEY))
				isArtFilterActive = true;

			StringBuilder selectedOptions = new StringBuilder();
			if (headerOrder.contains(facet.getId())) {
				for (Option option : facet.getSelectedOptions()) {
					selectedOptions.append(option.getValue());
				}
				map.put(headerOrder.indexOf(facet.getId()), selectedOptions.toString());
			}
		}

//		if (categoryKey != null && !isArtFilterActive)
//			map.put(headerOrder.indexOf(FacetsCacheManager.facets.NATURE_KEY), helper.t(categoryKey));

		int count = 1;
		List<String> headlineKeywords = new LinkedList<>();

		for (int pos : map.keySet()) {

			if (count > 1)
				headlineKeywords.add(" ");
			else if (headerOrder.get(pos).equals(FacetFilterFactory.GENERAL_PRICE_KEY))
				headlineKeywords.add("Preis: ");
			else if (headerOrder.get(pos).equals(FacetFilterFactory.MATERIAL_KEY))
				headlineKeywords.add("aus ");
			else if (headerOrder.get(pos).equals(FacetFilterFactory.AGE_KEY))
				headlineKeywords.add("ab ");
			else if (headerOrder.get(pos).equals(FacetFilterFactory.SEX_KEY))
				headlineKeywords.add("für ");
			else if (headerOrder.get(pos).equals(FacetFilterFactory.WEIGHT_KEY))
				headlineKeywords.add("Gewicht: ");
			else if (headerOrder.get(pos).equals(FacetFilterFactory.SIZE_KEY))
				headlineKeywords.add("in der Größe: ");

			headlineKeywords.add(map.get(pos));

			if (headerOrder.get(pos).equals(FacetFilterFactory.TEST_KEY))
				headlineKeywords.add(" Testsieger");
			else if (headerOrder.get(pos).equals(FacetFilterFactory.RANGE_KEY))
				headlineKeywords.add(" Reichweite");

			count++;

			if (count > 5)
				break;
		}

		return headlineKeywords;
	}

}