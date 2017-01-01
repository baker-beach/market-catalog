package com.bakerbeach.market.catalog.model;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bakerbeach.market.core.api.model.Option;

public class CategoryFilter extends AbstractFilter {

	public CategoryFilter(String id) {
		super(id);
	}

	@Override
	public String toUrl(Option currentOption) {
		StringBuilder url = new StringBuilder();

		try {
			List<Option> selectedOptions = getSelectedOptions();

			if (currentOption != null && this.equals(currentOption.getFilter())) {
				if (!selectedOptions.contains(currentOption)) {
					url.append("/").append(currentOption.getValue()).append("/");
				}
			} else {
				for (Option option : selectedOptions) {
					url.append("/").append(option.getValue()).append("/");
				}
			}
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}

		return url.toString();
	}

}
