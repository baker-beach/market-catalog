package com.bakerbeach.market.xcatalog.model;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bakerbeach.market.xcatalog.model.FacetOption;

public class CategoryFacetImpl extends AbstractFacet {

	public CategoryFacetImpl(String id) {
		super(id);
	}

	@Override
	public String toUrl(FacetOption currentOption) {
		StringBuilder url = new StringBuilder();

		try {
			List<FacetOption> selectedOptions = getSelectedOptions();

			if (currentOption != null && this.equals(currentOption.getFacet())) {
				if (!selectedOptions.contains(currentOption)) {
					url.append("/").append(currentOption.getValue()).append("/");
				}
			} else {
				for (FacetOption option : selectedOptions) {
					url.append("/").append(option.getValue()).append("/");
				}
			}
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}

		return url.toString();
	}

}
