package com.bakerbeach.market.xcatalog.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.xcatalog.model.Facet;
import com.bakerbeach.market.xcatalog.model.Facets;

public class FacetsImpl implements Facets {
	private Map<String, Facet> available = new LinkedHashMap<String, Facet>();

	@Override
	public Collection<Facet> getAvailable() {
		List<Facet> list = new ArrayList<Facet>(available.size());
		for (Facet filter : available.values()) {
//			if (filter instanceof FieldFilter || filter instanceof PriceRangeFilter) {
				list.add(filter);
//			}
		}
		return list;
//		return available.values();
	}
	
	@Override
	public void add(Facet filter) {
		available.put(filter.getId(), filter);
	}

	@Override
	public Collection<Facet> getActive() {
		return getActive(false);
	}
	
	@Override
	public Collection<Facet> getActive(Boolean url) {
		//TODO sort
		List<Facet> list = new ArrayList<Facet>(available.size());
		for (Facet filter : available.values()) {
			if (filter.isActive()) {
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Facet> getUrlRelevant() {
		List<Facet> list = new ArrayList<Facet>(available.size());
		
		for (Facet filter : available.values()) {
			if (filter.isUrlRelevant()) {
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Facet> getUrlRelevantIncluding(Facet _filter) {
		List<Facet> list = new ArrayList<Facet>(available.size());
		
		for (Facet filter : available.values()) {
			if (filter.isUrlRelevant() && (filter.isActive() || _filter != null && filter.getId().equals(_filter.getId()) && _filter.isUrlRelevant())) {
				list.add(filter);
			}
		}
		return list;
	}
	
	@Override
	public Collection<Facet> getUrlRelevantExcluding(Facet _filter) {
		List<Facet> list = new ArrayList<Facet>(available.size());

		for (Facet filter : available.values()) {
			if (filter.isUrlRelevant() && filter.isActive() && !filter.getId().equals(_filter.getId())) {
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Facet> getActiveFiltersWithGetParamtersIncluding(Facet _filter) {
		List<Facet> list = new ArrayList<Facet>(available.size());

		for (Facet filter : available.values()) {
			if (!filter.isUrlRelevant() && filter.isActive() || _filter != null && filter.getId().equals(_filter.getId()) && ! _filter.isUrlRelevant()){
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Facet> getActiveFiltersWithGetParamtersExcluding(Facet _filter) {
		List<Facet> list = new ArrayList<Facet>(available.size());

		for (Facet filter : available.values()) {
			if (!filter.isUrlRelevant() && filter.isActive() && !filter.getId().equals(_filter.getId())){
				list.add(filter);
			}
		}
		return list;
	}

	@Override
	public Collection<Facet> getInactive() {
		List<Facet> list = new ArrayList<Facet>(available.size());
		for (Facet filter : available.values()) {
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
	public Facet get(String id) {
		id = id.replaceFirst("_codes?$", "");
		return available.get(id);
	}

	@Override
	public void remove(String filterId) {
		available.remove(filterId);
	}

}