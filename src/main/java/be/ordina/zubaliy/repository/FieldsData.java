package be.ordina.zubaliy.repository;

public enum FieldsData {

	TIMESTAMP("timestamp"),
	LONGITUDE("longitude"),
	LATITUDE("latitude"),
	ACCURACY("accuracy"),
	SPEED("speed"),
	ALTITUDE("altitude"),
	ACCXAR("accxaR"),
	ACCYAR("accyaR"),
	ACCZAR("acczaR");

	private final String columnName;

	FieldsData(final String columnName) {
		this.columnName = columnName;
	}

	@Override
	public String toString(){
		return columnName;
	}



}
