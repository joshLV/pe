/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.csii.pe.extend;

import java.util.Map;

import com.csii.pe.transform.CurrentContextAware;
import com.csii.pe.transform.InnerProperties;
import com.csii.pe.transform.ReverseInnerProperties;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.stream.Field;
import com.csii.pe.validation.rule.OgnlExpr;

public class VariableTransformer extends Field
    implements CurrentContextAware
{

    public VariableTransformer()
    {
        cn = new ThreadLocal();
    }

    public Map getCurrentContext()
    {
        return (Map)cn.get();
    }

    public void setCurrentContext(Map map)
    {
        cn.set(map);
    }

    public Object format(Object obj, Map map)
        throws TransformException
    {
        Object obj1;
        OgnlExpr ognlexpr = new OgnlExpr();
        obj1 = ognlexpr.getValue(co, map, map);        
        if(cm != null && obj1 != null)
        {
            String s = obj1.toString();
            String s1 = (String)cu.get(s);
            if(s1 == null)
                s1 = (String)cu.get("@@@");
            s = s1 != null ? s1 : s;
            map.put(getName(), s);
        } else
        {
        	 map.put(getName(), obj1);
        }
        
        return new byte[0];
    }

    public Object parse(Object obj, Map map)
        throws TransformException
    {
        Map map1 = getCurrentContext();
        OgnlExpr ognlexpr = new OgnlExpr();
        Object obj1 = ognlexpr.getValue(co, map1, map1);
        if(cm != null && obj1 != null)
        {
            String s = obj1.toString();
            String s1 = (String)cm.get(s);
            if(s1 == null)
                s1 = (String)cm.get("@@@");
            s = s1 != null ? s1 : s;
            return s;
        } else
        {
            return obj1;
        }
    }

    public void setMapping(String s)
    {
        cm = new ReverseInnerProperties(s);
        cu = new InnerProperties(s);
    }

    public void setExpr(String s)
    {
        co = s;
    }

    private ThreadLocal cn;
    private String co;
    private Map cm;
    private Map cu;
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\ibs-fjnx-new\csii-pp-card\WebContent\WEB-INF\lib\pe-transform-guard.jar
	Total time: 143 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method format
	Exit status: 0
	Caught exceptions:
*/