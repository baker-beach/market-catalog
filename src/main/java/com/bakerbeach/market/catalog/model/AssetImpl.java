package com.bakerbeach.market.catalog.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bakerbeach.market.core.api.model.Asset;

public class AssetImpl implements Serializable, Asset {
	private static final long serialVersionUID = 1L;

	private String type;
	private String path;
	private Map<Locale, String> alt = new HashMap<Locale, String>(5);

	/* (non-Javadoc)
	 * @see com.bakerbeach.market.core.api.model.Asset#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see com.bakerbeach.market.core.api.model.Asset#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see com.bakerbeach.market.core.api.model.Asset#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see com.bakerbeach.market.core.api.model.Asset#setPath(java.lang.String)
	 */
	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see com.bakerbeach.market.core.api.model.Asset#getAlt()
	 */
	@Override
	public Map<Locale, String> getAlt() {
		return alt;
	}

	/* (non-Javadoc)
	 * @see com.bakerbeach.market.core.api.model.Asset#getAlt(java.util.Locale)
	 */
	@Override
	public String getAlt(Locale locale) {
		return alt.get(locale);
	}
	
	/* (non-Javadoc)
	 * @see com.bakerbeach.market.core.api.model.Asset#setAlt(java.util.Map)
	 */
	@Override
	public void setAlt(Map<Locale, String> alt) {
		this.alt = alt;
	}

}
