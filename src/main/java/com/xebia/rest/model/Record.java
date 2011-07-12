package com.xebia.rest.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class Record {
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String shortStringAttribute;
	
	@JsonProperty
	private String longStringAttribute;
	
	@JsonProperty
	private int intNumber;
	
	@JsonProperty
	private boolean trueOrFalse;
	
	public Record() {
		//Jackson requires this!
	}
	
	public Record(long id, String shortStringAttribute, String longStringAttribute, int intNumber, boolean trueOrFalse) {
		super();
		this.id = id;
		this.shortStringAttribute = shortStringAttribute;
		this.longStringAttribute = longStringAttribute;
		this.intNumber = intNumber;
		this.trueOrFalse = trueOrFalse;
	}

	public long getId() {
		return id;
	}

	public String getShortStringAttribute() {
		return shortStringAttribute;
	}

	public String getLongStringAttribute() {
		return longStringAttribute;
	}

	public int getIntNumber() {
		return intNumber;
	}

	public boolean isTrueOrFalse() {
		return trueOrFalse;
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", intNumber=" + intNumber + ", longStringAttribute=" + longStringAttribute + ", shortStringAttribute="
				+ shortStringAttribute + ", trueOrFalse=" + trueOrFalse + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
