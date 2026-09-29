package com.externalvisuals.setting;
public class ModeSetting extends Setting<String>{private final String[] values; public ModeSetting(String n,String d,String...v){super(n,d);values=v;} public String[] getValues(){return values;} public void cycle(){int i=0;for(int j=0;j<values.length;j++)if(values[j].equals(getValue()))i=j;setValue(values[(i+1)%values.length]);}}
