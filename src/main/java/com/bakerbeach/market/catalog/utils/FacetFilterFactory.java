package com.bakerbeach.market.catalog.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.catalog.model.CategoryFilter;
import com.bakerbeach.market.catalog.model.FacetFilterList;
import com.bakerbeach.market.catalog.model.FieldFilter;
import com.bakerbeach.market.catalog.model.PriceRangeFilter;
import com.bakerbeach.market.core.api.model.Filter;
import com.bakerbeach.market.core.api.model.FilterList;

public class FacetFilterFactory {

	public static final String CATEGORY_KEY = "category";
	public static final String NATURE_KEY = "nature";
	public static final String BRAND_KEY = "brand";
	public static final String COLOR_KEY = "color";
	public static final String COLORPICKER_KEY = "colorpicker";	
	public static final String SEX_KEY = "gender";
	// public static final String SIZE_KEY = "default_available_size";
	public static final String SIZE_KEY = "size";
	public static final String MATERIAL_KEY = "material";
	public static final String AGE_KEY = "minimum_age";
	public static final String TEST_KEY = "tests";
	public static final String ISOFIX_KEY = "isofix";
	public static final String WEIGHT_KEY = "weight";
	public static final String FOOD_KEY = "nutritional_characteristics";
	public static final String RANGE_KEY = "range";
	public static final String WHEELS_KEY = "wheels";
	public static final String OTHER_KEY = "other";
	public static final String SEASON_KEY = "season";
	public static final String THEME_KEY = "theme";
	public static final String FILLING_KEY = "filling";
	public static final String ACCESSORIES_KEY = "accessory";
	public static final String CHARACTERISTICS_KEY = "characteristics";
	public static final String TOY_AGE_KEY = "toy_age";
	public static final String SIGNET_KEY = "signet";
	public static final String BEDDING_SIZE_KEY = "bettwaeschengroesse";
	public static final String FIXMOLTON_SIZE_KEY = "fixmolton_groesse";
	public static final String SHOE_SIZE_KEY = "schuhgroesse";
	public static final String SLEEPING_BAG_SIZE_KEY = "schlafsackgroesse";
	public static final String HEAD_SIZE_KEY = "muetzengroesse";
	public static final String CUP_SIZE_KEY = "koerbchengroesse";
	public static final String DIAPERS_SIZE_KEY = "windelgroesse"; // "windelgroesse";?
	public static final String WEIGHT_CATEGORY_KEY = "weight_category";
	public static final String BOTTLE_SIZE_KEY = "flaschengroesse_fuellmenge";
	// public static final String _KEY ="kinderbett_groesse";
	// public static final String _KEY ="wickelauflage_groesse";
	// public static final String _KEY ="einheitsgroesse";
	// public static final String _KEY ="stoffwindel_groesse";
	// public static final String _KEY ="kleidergroesse";
	// public static final String _KEY ="erwachsenen_groessen";

	public static final String SORT_PARAM = "sort";
	public static final String PAGE_PARAM = "page";
	public static final String PAGE_SIZE_PARAM = "pagesize";
	public static final String MIN_PRICE = "min_price";
	public static final String MAX_PRICE = "max_price";
	public static final String SALE_FLAG = "sale";

	public static final String URL_ENCODED_ATTRIBUTES = "urlEncodedAttributes";

	private static final List<String> IGNORED_GET_KEYS = Arrays.asList(SORT_PARAM, PAGE_PARAM, PAGE_SIZE_PARAM);

	public static final String GENERAL_PRICE_KEY = "price";

	// private static final String[] DEFAULT_FIELD_FILTERS = { NATURE_KEY,
	// BRAND_KEY, COLOR_KEY, SEX_KEY, SIZE_KEY, MATERIAL_KEY,
	// SEASON_KEY, FILLING_KEY, SIGNET_KEY, HEAD_SIZE_KEY };
	// private static final String[] FIELD_FILTERS = { NATURE_KEY, BRAND_KEY,
	// COLOR_KEY, SEX_KEY, SIZE_KEY, MATERIAL_KEY, AGE_KEY, TEST_KEY,
	// ISOFIX_KEY, WEIGHT_KEY, FOOD_KEY, RANGE_KEY, WHEELS_KEY,
	// OTHER_KEY, SEASON_KEY, THEME_KEY, FILLING_KEY, ACCESSORIES_KEY,
	// CHARACTERISTICS_KEY, TOY_AGE_KEY, SIGNET_KEY, BEDDING_SIZE_KEY,
	// FIXMOLTON_SIZE_KEY, SHOE_SIZE_KEY, SLEEPING_BAG_SIZE_KEY, HEAD_SIZE_KEY,
	// CUP_SIZE_KEY, DIAPERS_SIZE_KEY,
	// WEIGHT_CATEGORY_KEY, BOTTLE_SIZE_KEY };


	public static final List<String> HEADLINE_FILTERS = new ArrayList<String>() {
		{
			add(BRAND_KEY);
			// add(CATEGORY_KEY);
			add(NATURE_KEY);
			add(MATERIAL_KEY);
			add(SEX_KEY);
			add(SIZE_KEY);
			add(AGE_KEY);
			add(ISOFIX_KEY);
			add(TEST_KEY);
			add(FOOD_KEY);
			add(WEIGHT_KEY);
			add(OTHER_KEY);
			add(WHEELS_KEY);
			add(RANGE_KEY);
			add(GENERAL_PRICE_KEY);
		}
	};

	private static final List<String> DEFAULT_FIELD_FILTERS = Arrays.asList(COLORPICKER_KEY, SIZE_KEY);
	private static List<String> fieldFilters = DEFAULT_FIELD_FILTERS;

	private static final List<String> DEFAULT_SINGLE_SELECT_FILTERS = Arrays.asList(CATEGORY_KEY);
	private static List<String> singleSelectFilters = DEFAULT_SINGLE_SELECT_FILTERS;

	private static final List<String> DEFAULT_MULTI_VALUE_FILTERS = Arrays.asList(CATEGORY_KEY);
	private static List<String> multiValueFilters = DEFAULT_MULTI_VALUE_FILTERS;

	public static final List<String> DEFAULT_URL_RELEVANT_FILTERS = Arrays.asList(CATEGORY_KEY);
	public static List<String> urlRelevantFilters = DEFAULT_URL_RELEVANT_FILTERS;

	public static FilterList newInstance(Boolean getParamtersOnly) {
		FilterList filterList = new FacetFilterList(HEADLINE_FILTERS);

		PriceRangeFilter priceRangeFilter = new PriceRangeFilter(GENERAL_PRICE_KEY);
		filterList.add(priceRangeFilter);

		Filter categoryFilter = new CategoryFilter(CATEGORY_KEY);
		if (!getParamtersOnly)
			categoryFilter.setUrlRelevant(true);
		categoryFilter.setMultiValue(true);
		filterList.add(categoryFilter);

		for (String id : fieldFilters) {
			Filter filter = new FieldFilter(id);
			filterList.add(filter);

			if (singleSelectFilters.contains(id))
				filter.setMultiSelect(false);
			else
				filter.setMultiSelect(true);

			if (multiValueFilters.contains(id))
				filter.setMultiValue(true);
			else
				filter.setMultiValue(false);

			if (!getParamtersOnly && urlRelevantFilters.contains(id)) {
				filter.setUrlRelevant(true);
				filter.setMultiSelect(false);
			}
		}
		
		return filterList;
	}

	public static FilterList newInstanceCategoryBrowsing(Map<String, String[]> filterParameters, String lang) {
		FilterList filterList = newInstance(false);
		initFilterList(filterList, filterParameters, lang);
		return filterList;
	}

	public static FilterList newInstanceSearch(Map<String, String[]> filterParameters, String lang) {
		FilterList filterList = newInstance(true);
		initFilterList(filterList, filterParameters, lang);
		return filterList;
	}

	private static void initFilterList(FilterList filterList, Map<String, String[]> filterParameters, String lang) {
		/*
		 * Map<String, String> attributeUrlMap =
		 * attributeUrlMapping.getAttributeUrlMapping(lang);
		 * 
		 * PriceRangeFilter priceRangeFilter = new
		 * PriceRangeFilter(GENERAL_PRICE_KEY);
		 * filterList.add(priceRangeFilter);
		 * 
		 * for (Map.Entry<String, String[]> filterParameter : filterParameters
		 * .entrySet()) { String parameter = filterParameter.getKey(); if
		 * (!IGNORED_GET_KEYS.contains(parameter)) { if
		 * (parameter.equals(MIN_PRICE)){ Option option = new
		 * FieldOption(filterParameter.getValue()[0], MIN_PRICE, null, true);
		 * priceRangeFilter.addOption(option); priceRangeFilter.setActive(true);
		 * }else if (parameter.equals(MAX_PRICE)){ Option option = new
		 * FieldOption(filterParameter.getValue()[0], MAX_PRICE, null, true);
		 * priceRangeFilter.addOption(option); priceRangeFilter.setActive(true);
		 * }else if (parameter.equals(SALE_FLAG)){ Option option = new
		 * FieldOption(filterParameter.getValue()[0], SALE_FLAG, null, true);
		 * priceRangeFilter.addOption(option); priceRangeFilter.setActive(true);
		 * } else if(parameter.equals(URL_ENCODED_ATTRIBUTES)) { for (String
		 * value : filterParameter.getValue()) {
		 * 
		 * if (attributeUrlMap.containsKey(value)) { String urlCode =
		 * attributeUrlMap.get(value); String code =
		 * urlCode.replaceFirst("\\.url$", ""); String filterId =
		 * code.substring(code.lastIndexOf(".") + 1);
		 * 
		 * Filter filter = filterList.get(filterId); if (filter != null) {
		 * Option option = new FieldOption(code, urlCode, null, true);
		 * filter.addOption(option); filter.setActive(true); } } } } else { for
		 * (String value : filterParameter.getValue()) { if
		 * (attributeUrlMap.containsKey(value)) { String urlCode =
		 * attributeUrlMap.get(value); String code =
		 * urlCode.replaceFirst("\\.url$", ""); String filterId =
		 * code.substring(code.lastIndexOf(".") + 1);
		 * 
		 * Filter filter = filterList.get(filterId); if (filter != null &&
		 * parameter.equals(filterId)) { Option option = new FieldOption(code,
		 * urlCode, null, true); filter.addOption(option);
		 * filter.setActive(true); } } } } } }
		 */
	}
}