package com.bakerbeach.market.catalog.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.catalog.service.CatalogServiceImpl;
import com.bakerbeach.market.core.api.model.Product;

public class VariantsStdImpl implements Variants {
	protected static final Logger log = LoggerFactory.getLogger(VariantsStdImpl.class);
	
	private SortedMap<Key, SortedMap<Key, List<Product>>> map = new TreeMap<Key, SortedMap<Key, List<Product>>>();
	private String x;
	private String y;
	private Type type;
	
	public VariantsStdImpl(String x, String y) {
		this.x = x;
		this.y = y;
		
		if (this.x != null && this.y != null) {
			type = Type.XY;
		} else if (this.x != null) {
			type = Type.X;
		} else if (this.y != null) {
			type = Type.Y;
		} else {
			type = Type.NONE;
		}
	}
	
	@Override
	public String getX() {
		return x;
	}
	
	@Override
	public String getY() {
		return y;
	}
	
	@Override
	public List<Product> get(Key x, Key y) {
		return map.get(x).get(y);
	}
	
	@Override
	public Set<Key> getXValues() {
		return map.keySet();
	}
	
	@Override
	public Set<Key> getYValues(Key x) {
		return map.get(x).keySet();
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void add(Product product) {
		try {
			String xValue = null;
			String yValue = null;
			
			if (x != null && !x.isEmpty()) {
				xValue = BeanUtils.getSimpleProperty(product, x);				
			}
			if (y != null && !y.isEmpty()) {
				yValue = BeanUtils.getSimpleProperty(product, y);				
			}
			
			KeyStdImpl i = new KeyStdImpl(xValue, xValue);
			KeyStdImpl j = new KeyStdImpl(yValue, yValue);
			if (!map.containsKey(i)) {
				map.put(i, new TreeMap<Key, List<Product>>());
			}
			if (!map.get(i).containsKey(j)) {
				map.get(i).put(j, new ArrayList<Product>());
			}
			map.get(i).get(j).add(product);
			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public int size() {
		return map.size();
	}

	public int getSize() {
		return map.size();
	}
	
	private static class KeyStdImpl implements Variants.Key {
		private String value;
		private String sort;
		
		public KeyStdImpl(String value, String sort) {
			this.value = value;
			this.sort = sort;
		}

		@Override
		public int compareTo(Key other) {
			if (other != null) {
				if (this.sort != null && other.getSort() != null)
					return this.sort.compareTo(other.getSort());
				if (this.sort == null && other.getSort() == null)
					return 0;
				if (this.sort == null)
					return -1;
				return 1;
			}
			return 0;
		}

		@Override
		public String toString() {
			return (value != null)? value : "null";
		}
		
		@Override
		public String getSort() {
			return sort;
		}
		
		@Override
		public String getValue() {
			return value;
		}
		
	}
	
}

