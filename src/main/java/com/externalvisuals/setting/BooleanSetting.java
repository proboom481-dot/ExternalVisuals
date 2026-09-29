package com.externalvisuals.setting;
public class BooleanSetting extends Setting<Boolean>{ public BooleanSetting(String n,boolean v){super(n,v);} public boolean isEnabled(){return getValue();} public void toggle(){setValue(!getValue());} }
