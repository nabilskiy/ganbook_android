package com.ganbook.communication.json;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class BaseResponse {	
	private static final boolean CLEAR_ORIG_STR_FIELD = true; 
	
	public void postLoad() {}

	@Override
	public String toString() {
		Class<?> cl = this.getClass();
		String r = cl.getName();
		do {
			r += "[";
			Field[] fields = cl.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			// get the names and values of all fields
			for (Field f: fields) {
				int _mod = f.getModifiers();
				if (!Modifier.isStatic(_mod) && Modifier.isPublic(_mod)) {
					if (!r.endsWith("[")) { 
						r += ",";
					}
					r += f.getName() + "=";
					try {
						Class t = f.getType();
						Object val = f.get(this);
						if (val==null) val = "null"; 
						if (t.isPrimitive()) 
							r += val;
						else 
							r += val.toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			r += "]";
			cl = cl.getSuperclass();
		} while (cl != null);

		return r;	
	}
	
	protected final void toInt(String stringParamName) {
		if (stringParamName == null) {
			throw new RuntimeException();
		}		
		Field strField;
		Field intField;
		String strValue;
		try {
			strField = this.getClass().getDeclaredField(stringParamName);
			if (strField == null) {
				throw new RuntimeException();
			}			
			strField.setAccessible(true); 
			intField = this.getClass().getField(stringParamName + "_val");
			if (intField == null) {
				throw new RuntimeException();
			}					
			intField.setAccessible(true); 
			Object _value = strField.get(this);
			if (_value == null || !(_value instanceof String)) {
				throw new RuntimeException();
			}			
			strValue = (String)_value;
		} 
		catch (Exception e) {
			int jj=234;
			jj++;
			throw new RuntimeException(e);
		}
		
		
		int intVal = 0;
		try {
			intVal = Integer.parseInt(strValue);
		}
		catch (Exception e) {
			int jj=234;
			jj++;
			// do not re-throw
		}
		
		try {
			intField.setInt(this, intVal);
		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		
		if (CLEAR_ORIG_STR_FIELD) {
			try {
				strField.set(this, null);
			} catch (Exception e) {
				int jj=234;
				jj++;
				e.printStackTrace();
			} 
		}

	}

}

