package com.bakerbeach.market.catalog.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.core.api.model.Filter;
import com.bakerbeach.market.core.api.model.Option;

public abstract class AbstractFilter implements Filter {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractFilter.class.getName());
	
	protected String id;
	protected String translationKey;
	protected String label;
	protected Boolean active = false;
	protected Boolean multiSelect = false;
	protected Boolean urlRelevant = false;
	protected Boolean multiValue = false;
	protected List<Option> options = new ArrayList<Option>();
	protected Map<String, Option> optionsMap = new HashMap<>();

	public AbstractFilter(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIndexFieldName() {
		if(this instanceof PriceRangeFilter)
			return id;
		if (multiValue)
			return id + "_codes";
		else
			return id + "_code";
	}

	@Override
	public String getLabel() {
		return "attribute." + getId();
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Boolean isActive() {
		return active;
	}

	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public Boolean hasOptions() {
		return !options.isEmpty();
	}

	@Override
	public List<Option> getOptions() {
		return options;
	}

	@Override
	public void addOption(Option option) {
		option.setFilter(this);
		options.add(option);
		optionsMap.put(option.getCode(), option);
	}

	@Override
	public List<Option> getSelectedOptions() {
		List<Option> list = new ArrayList<Option>(options.size());
		for (Option option : options) {
			if (option.isSelected()) {
				list.add(option);
			}
		}

		return list;
	}

	@Override
	public Option getOption(String translationKey){
		if(optionsMap.containsKey(translationKey))
			return optionsMap.get(translationKey);
		return null;
	}

	@Override
	public Boolean isMultiSelect() {
		return multiSelect;
	}

	@Override
	public void setMultiSelect(Boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	@Override
	public void setUrlRelevant(Boolean urlRelevant) {
		this.urlRelevant = urlRelevant;
	}

	@Override
	public Boolean isUrlRelevant() {
		return urlRelevant;
	}
	
	@Override
	public String toUrl(Option currentOption) {
		StringBuilder url = new StringBuilder();
		try {
			List<Option> selectedOptions = this.getSelectedOptions();
			if (currentOption!= null && this.equals(currentOption.getFilter())) {
				if (this.isMultiSelect()) {
					if (selectedOptions.contains(currentOption)) {
						selectedOptions.remove(currentOption);
					} else {
						selectedOptions.add(currentOption);
					}
					for (Option option : selectedOptions) {
						url.append("_").append(option.getValue());
					}
				} else {
					if (!selectedOptions.contains(currentOption)) {
						url.append("_").append(currentOption.getValue());
					}
				}
			} else {
				for (Option option : selectedOptions) {
					url.append("_").append(option.getValue());
				}
			}
			
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		
		return url.toString();
	}
	
	@Override
	public String toGetParameter(Option currentOption) {
		String filterId = id;
		StringBuilder params = new StringBuilder();
		
		try {
			List<Option> selectedOptions = this.getSelectedOptions();
			
			if (currentOption != null && this.equals(currentOption.getFilter())) {
				if (this.isMultiSelect()) {	
					if (selectedOptions.contains(currentOption)) {
						selectedOptions.remove(currentOption);
					} else {
						selectedOptions.add(currentOption);
						// TODO: sort
					}
					
					for (Option option : selectedOptions) {
						params.append("&").append(filterId).append("=").append(option.getValue());
					}					
				} else {
					if (!selectedOptions.contains(currentOption)) {
						params.append("&").append(filterId).append("=").append(currentOption.getValue());
					}					
				}
			} else {
				for (Option option : selectedOptions) {
					params.append("&").append(filterId).append("=").append(option.getValue());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return params.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Filter) {
			Filter other = (Filter) obj;
			return this.getId().equals(other.getId());
		} else {
			return false;			
		}
	}

	public Boolean getMultiSelect() {
		return multiSelect;
	}

	public Boolean getUrlRelevant() {
		return urlRelevant;
	}

	@Override
	public Boolean getMultiValue() {
		return multiValue;
	}

	@Override
	public void setMultiValue(Boolean multiValue) {
		this.multiValue = multiValue;
	}

}
