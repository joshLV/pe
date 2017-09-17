package com.csii.pe.extend;
import com.csii.pe.transform.stream.field.AmountTransformer;
import com.csii.pe.transform.stream.var.VariableObject;
public class VariableAmount2 extends VariableObject{
    public VariableAmount2()
    {
         bT = new AmountTransformer2();
        setField(bT);
    }
    
    public int getDecimal()
    {
        return bT.getDecimal();
    }

    public void setDecimal(int i)
    {
        bT.setDecimal(i);
    }

    public boolean isSigned()
    {
        return bT.isSigned();
    }

    public void setSigned(boolean flag)
    {
        bT.setSigned(flag);
    }

    public boolean isWithDelimeter()
    {
        return bT.isWithDelimeter();
    }

    public void setWithDelimeter(boolean flag)
    {
        bT.setWithDelimeter(flag);
    }

    private AmountTransformer bT;
	
}
