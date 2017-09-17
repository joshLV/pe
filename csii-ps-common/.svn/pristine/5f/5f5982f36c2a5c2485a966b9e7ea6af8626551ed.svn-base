package com.csii.pe.extend;

import java.math.BigDecimal;
import java.util.Map;

import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.stream.field.AmountTransformer;

public class AmountTransformer2 extends AmountTransformer {
	/**
	 * 去掉左边0
	 */
	 public Object format(Object obj, Map map)
	       throws TransformException
	 {
			try {
				byte[] bytes = (byte[]) super.format(obj, map);
				BigDecimal bigDecimal=new BigDecimal(new String(bytes, getEncoding()));
				return bigDecimal.toString().getBytes(getEncoding());
				
			} catch (Exception exception) {
				throw new TransformException(getClass().getName() + ".format error: " + getName(),
						exception);
			}

		}

}
