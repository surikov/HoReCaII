package sweetlife.android10.update;

import java.util.Enumeration;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sweetlife.android10.consts.IDeltaTags;
import sweetlife.android10.consts.ISQLConsts;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.update.QueryInfo.QueryType;
import sweetlife.android10.utils.Hex;
import tee.binding.Bough;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DeltaParser extends DefaultHandler implements IDeltaTags, ISQLConsts {
	private DeltaData mDeltaData;
	private int mRecordsCount;
	private boolean mIsComplete;
	private boolean mHasHeaderParseErrors;
	private boolean mHasBodyParseErrors;
	private boolean mIsHeader;
	private String mCurrentHeaderNode;
	private TableInfo mCurrentTable;
	String preTable = "";
	private String mXMLTableName;
	private TablesInfo mTablesInfo;
	private QueriesList mQueriesList;
	private QueryInfo mNewQuery;
	private StringBuilder mQueryWhereCondition;
	private boolean mHasRecorder;
	private boolean mHasFilter;
	private SQLiteDatabase mDB;
	Bough allValues = new Bough();
	private TempTablesInfo mTempTablesInfo;
	String specDeletionMark = "false";
	//String specVladelec = "false";
	String specPriceNumber = "-1";
	ContentValues preValues = new ContentValues();
	String pre_IDDRef = "";
	final static String fakePriceVladelecKey = "12345678901234567890123456789012";
	Vector<String> path = new Vector<String>();
	Bough values = new Bough();
	//String preNodeName="";
	DeltaParser(TablesInfo tablesInfo, QueriesList queriesList, SQLiteDatabase db, TempTablesInfo tempTablesInfo) {
		mTablesInfo = tablesInfo;
		mQueriesList = queriesList;
		mDB = db;
		mTempTablesInfo = tempTablesInfo;
	}
	@Override
	public void startDocument() throws SAXException {
		mRecordsCount = 0;
		mDeltaData = new DeltaData();
		mIsComplete = false;
		mHasHeaderParseErrors = false;
		mHasBodyParseErrors = false;
		mIsHeader = false;
		mCurrentHeaderNode = null;
		mCurrentTable = null;
		mXMLTableName = null;
		mNewQuery = null;
		mHasRecorder = false;
		mHasFilter = false;
		mQueryWhereCondition = new StringBuilder();
		sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + " startDocument");
		String sql = "delete from Price where Vladelec=x'" + fakePriceVladelecKey + "'";
		//specVladelec;
		//System.out.println("adjust temp Прайс - " + sql);
		mDB.execSQL(sql);
		sql = "delete from KartaKlienta where number=" + fakePriceVladelecKey + ";";
		mDB.execSQL(sql);
	}
	@Override
	public void endDocument() throws SAXException {
		mQueriesList.ExecuteQueries();
		setIsComplete(true);
		sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + " endDocument");
	}
	public int getRecordsCount() {
		return mRecordsCount;
	}
	String dumpPath(Vector<String> path) {
		String r = "";
		for (int i = 0; i < path.size(); i++) {
			r = r + " / " + path.get(i);
		}
		return r;
	}
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		path.add(localName);
		int attsCount = atts.getLength();
		for (int i = 0; i < attsCount; i++) {
			//field = mCurrentTable.getFields().get(atts.getLocalName(i));
			allValues.child(atts.getLocalName(i)).value.property.value(atts.getValue(i));
		}
		/*while (mQueriesList.getCount() > 500) {
			try {
				sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + " sleep");
				Thread.sleep(999);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		//if (!localName.equals(NODE_LINE)) {
		//System.out.println("startElement " + localName + " /" + (mCurrentTable == null ? "null" : mCurrentTable.getSQLTableName()));
		//}
		try {
			if (NODE_FILTER.compareTo(localName) == 0) {
				if (atts.getLocalName(0).compareTo(ATTRIBUTE_RECORDER) == 0) {
					DeleteFilterNode(atts);
					mHasRecorder = true;
				}
				else {
					CreateWhereCondition(atts);
				}
			}
			else {
				if (NODE_RECORD.compareTo(localName) == 0) {
					if (mHasRecorder) {
						CreateQueryByAttrsForInsert(atts);
					}
					else {
						if (mHasFilter) {
							CreateQueryByAttrsWithFilter(atts);
						}
						else {
							CreateQueryByAttrsWithWhereCondition(atts);
						}
					}
				}
				else {
					if (NODE_LINE.compareTo(localName) == 0) {
						//System.out.println("1" + dumpPath(path));
						/*if (path.size() == 5 //
								&& path.get(path.size() - 1).equals("line")//
								&& (path.get(path.size() - 2).equals("Владельцы") || path.get(path.size() - 2).equals("Товары"))//
								&& path.get(path.size() - 3).equals("DocumentObject.КартаКлиента")//
						) {
							//parseKartaKlientaLine(atts);
							if (path.get(path.size() - 2).equals("Владельцы")) {
								parseKartaKlientaKontragent(atts);
							}
							else {
								if (path.get(path.size() - 2).equals("Товары")) {
									parseKartaKlientaNomenklatura(atts);
								}
							}
						}
						*/
						
							if (path.size() == 5 //
									&& path.get(path.size() - 1).equals("line")//
									&& path.get(path.size() - 2).equals("Владельцы") //
									&& path.get(path.size() - 3).equals("DocumentObject.КартаКлиента")//
							) {
								parseKartaKlientaKlient();
							}
							else {
								if (path.size() == 5 //
										&& path.get(path.size() - 1).equals("line")//
										&& path.get(path.size() - 2).equals("Товары")//
										&& path.get(path.size() - 3).equals("DocumentObject.КартаКлиента")//
								) {
									parseKartaKlientaNomenklatura();
								}
								else {
									if (path.size() == 5 //
											&& path.get(path.size() - 1).equals("line")//
											&& path.get(path.size() - 2).equals("Владельцы")//
									) {
										parsePriceVladelecLine(atts);
									}
									else {
										if (path.size() == 5 //
												&& path.get(path.size() - 1).equals("line")//
												&& path.get(path.size() - 2).equals("Ассортимент")//
										) {
											//parsePriceDataLine(atts);
											CreateQueryByRefWithWhereConditionAndReplaceVladelec(atts);
										}
										else {
											CreateQueryByRefWithWhereConditionOnly(atts);
										}
									}
								
							}
						}
					}
					else {
						if (path.size() == 3 && path.get(path.size() - 1).equals("DocumentObject.КартаКлиента")) {
							parseKartaKlientaDok();
						}
						else {
						if (NODE_DELETE_FILTER.compareTo(localName) == 0 && atts.getLength() > 0) {
							DeleteFilterNode(atts);
						}
						else {
							if (NODE_DELETE_OBJECT.compareTo(localName) == 0 && atts.getLength() > 0) {
								DeleteObjectNode(atts);
							}
							else {
								if (mIsHeader) {
									mCurrentHeaderNode = qName;
								}
								else {
									if (NODE_HEADER.compareTo(qName) == 0) {
										mIsHeader = true;
									}
									else {
										if (mCurrentTable == null) {
											SetNewTableName(localName, atts);
											if (atts.getLength() > 0) {
												if (mCurrentTable != null) {
													if (localName.equals("DocumentObject.Прайс") || localName.equals("DocumentObject.КартаКлиента")) {
														UpdateSpecNode(atts);
													}
													else {
														UpdateNode(atts);
													}
												}
												else {
													ConstNode(localName, atts);
												}
											}
										}
									}
								}
							}}
						}
					}
				}
			}
		}
		catch (Exception e) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE startElement " + mCurrentTable.getSQLTableName() + ": " + e);
			mHasBodyParseErrors = true;
			e.printStackTrace();
		}
		//preNodeName=localName;
	}
	private void SetNewTableName(String name, Attributes atts) {
		if (!preTable.equals(name)) {
			preTable = name;
			LogHelper.debug(this.getClass().getCanonicalName() + ": SetNewTableName " + name);
		}
		mCurrentTable = mTablesInfo.getTable(name);
		if (mCurrentTable != null) {
			//System.out.println("found " + mCurrentTable.getSQLTableName());
			mTempTablesInfo.checkTable(name);
			mXMLTableName = name;
			//rememberAttributes(atts);
			rememberPre_IDDRef(atts);
		}
		else {
			if (name.compareToIgnoreCase(NODE_MESSAGE) != 0 && name.compareToIgnoreCase(NODE_BODY) != 0) {
				sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + " SetNewTableName not found " + name);
				mHasBodyParseErrors = true;
			}
		}
	}
	void UpdateSpecNode(Attributes atts) {
		if (mCurrentTable != null) {
			//System.out.println("UpdateSpecNode, mCurrentTable: " + mCurrentTable.getSQLTableName());
			int nn = atts.getLength();

			try {
				specDeletionMark = atts.getValue(atts.getIndex("DeletionMark"));
				specPriceNumber = atts.getValue(atts.getIndex("Number"));
				/*if (specVladelec.length() < 5) {
					specVladelec = " is null";
				} else {
					specVladelec = "=x'" + specVladelec + "'";
				}*/
				/*String sql = "select count(*) as nn,vladelec, p.naimenovanie,r.number "//
						+ " from price r"// 
						+ " left join Podrazdeleniya p on r.vladelec=p._idrref"//
						+ " group by r.vladelec"//
						+ " order by r.number";
				System.out.println(Auxiliary.fromCursor(mDB.rawQuery(sql, null)).dumpXML());*/
				//System.out.println(sql);
				//mDB.execSQL(sql);
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			//System.out.println("done UpdateSpecNode, mCurrentTable: " + mCurrentTable.getSQLTableName());
		}
	}
	void rememberPre_IDDRef(Attributes atts) {
		if (atts != null) {
			String x = atts.getValue("Ref");
			if (x != null) {
				pre_IDDRef = "x'" + x + "'";
			}
			int attsCount = atts.getLength();
			if (attsCount > 0) {
				String name;
				String value;
				TableField field = null;
				ContentValues mValues = new ContentValues();
				for (int i = 0; i < attsCount; i++) {
					name = atts.getLocalName(i);
					value = atts.getValue(name);
					field = mCurrentTable.getFields().get(atts.getLocalName(i));
					if (field != null) {
						//System.out.println(atts.getLocalName(i) + " / " + field.getType());
						switch (field.getType()) {
							case BLOB_TYPE:
								mValues.put("[" + field.getName() + "]", ConvertToBlob(atts.getValue(i), field.getName()));
								break;
							case DOUBLE_TYPE:
								if (atts.getValue(i).length() != 0)
									mValues.put("[" + field.getName() + "]", Double.parseDouble(atts.getValue(i)));
								break;
							default:
								mValues.put("[" + field.getName() + "]", atts.getValue(i));
								break;
						}
					}
				}
				preValues = mValues;
			}
		}
	}
	private void UpdateNode(Attributes atts) {
		//System.out.println("UpdateNode ");
		if (mCurrentTable != null) {
			//System.out.println(" -- " + mCurrentTable.getSQLTableName());
			mQueryWhereCondition = mQueryWhereCondition.delete(0, mQueryWhereCondition.length());
			TableField field = mCurrentTable.getFields().get(atts.getLocalName(0));
			if (field != null) {
				mQueryWhereCondition.append("[" + field.getName() + "]=" + ConvertForConditions(atts.getValue(0)));
				//System.out.println(mQueryWhereCondition);
			}
			else {
				sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE UpdateNode");
				mHasBodyParseErrors = true;
			}
			CreateQueryByAttrsWithFilter(atts);
			mCurrentTable = null;
		}
	}
	private void ConstNode(String localName, Attributes atts) {
		//System.out.println("ConstNode " + localName);
		mCurrentTable = mTablesInfo.getTable("Константы.Consts");
		if (mCurrentTable != null) {
			try {
				int pointIndex = localName.lastIndexOf(".");
				if (pointIndex >= 0) {
					TableField field = mCurrentTable.getFields().get(localName.substring(pointIndex + 1, localName.length()));
					if (field != null) {
						mNewQuery = new QueryInfo();
						byte[] blob = ConvertToBlob(atts.getValue(0), field.getName());
						if (blob != null) {
							mNewQuery.getValues().put("[" + field.getName() + "]", blob);
						}
					}
					else {
						sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE ConstNode1 " + mCurrentTable.getSQLTableName());
						mHasBodyParseErrors = true;
					}
				}
				else {
					sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE ConstNode2 " + mCurrentTable.getSQLTableName());
					mHasBodyParseErrors = true;
				}
				mNewQuery.setTableName(mCurrentTable.getSQLTableName());
				mNewQuery.setType(QueryType.qtUPDATE);
				if (mNewQuery.IsReady()) {
					synchronized (mQueriesList) {
						mQueriesList.putQuery(mNewQuery);
					}
					mRecordsCount++;
				}
			}
			catch (Exception e) {
				sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE ConstNode3 " + mCurrentTable.getSQLTableName());
				mHasBodyParseErrors = true;
				e.printStackTrace();
			}
			finally {
				mCurrentTable = null;
				mNewQuery = null;
			}
		}
	}
	private void DeleteObjectNode(Attributes atts) {
		try {
			SetNewTableName(GetTableNameForDelete(atts.getLocalName(0)), atts);
			if (mCurrentTable != null) {
				if (atts.getLength() == 0) {
					sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + " SWLIFE DeleteObjectNode");
					return;
				}
				mDB.delete(mCurrentTable.getSQLTableName(), "[" + PRIMARY_KEY_1C + "]=" + ConvertForConditions(atts.getValue(0)), null);
				mRecordsCount++;
			}
		}
		catch (Exception e) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE DeleteObjectNode " + mCurrentTable.getSQLTableName());
			mHasBodyParseErrors = true;
			e.printStackTrace();
		}
	}
	private String GetTableNameForDelete(String name) {
		String key = null;
		Enumeration<String> keys = mTablesInfo.getRelationsTables().keys();
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			if (key != null && key.endsWith(name)) {
				return key;
			}
		}
		sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE GetTableNameForDelete " + mCurrentTable.getSQLTableName());
		return null;
	}
	private void DeleteFilterNode(Attributes atts) {
		if (mCurrentTable != null) {
			try {
				CreateWhereCondition(atts);
				if (mQueryWhereCondition.length() == 0) {
					sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE DeleteFilterNode");
					return;
				}
				mDB.delete(mCurrentTable.getSQLTableName(), mQueryWhereCondition.toString(), null);
				mRecordsCount++;
			}
			catch (Exception e) {
				sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE DeleteFilterNode " + mCurrentTable.getSQLTableName());
				mHasBodyParseErrors = true;
				e.printStackTrace();
			}
		}
	}
	private void CreateQueryByAttrsWithFilter(Attributes atts) {
		//System.out.println("CreateQueryByAttrsWithFilter");
		if (mCurrentTable == null) {
			//Log.v("sw",this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithFilter1");
			mHasBodyParseErrors = true;
			return;
		}
		int attsCount = atts.getLength();
		try {
			mNewQuery = new QueryInfo();
			TableField field = null;
			for (int i = 0; i < attsCount; i++) {
				field = mCurrentTable.getFields().get(atts.getLocalName(i));
				if (field != null) {
					switch (field.getType()) {
						case BLOB_TYPE:
							//System.out.println(mNewQuery.getTableName()+" . "+field.getName()+" = "+ConvertToBlob(atts.getValue(i), field.getName()));
							//
							mNewQuery.getValues().put("[" + field.getName() + "]", ConvertToBlob(atts.getValue(i), field.getName()));
							break;
						case DOUBLE_TYPE:
							if (atts.getValue(i).length() != 0)
								mNewQuery.getValues().put("[" + field.getName() + "]", Double.parseDouble(atts.getValue(i)));
							break;
						default:
							mNewQuery.getValues().put("[" + field.getName() + "]", atts.getValue(i));
							break;
					}
				}
				else {
					//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithFilter2 " + mCurrentTable.getSQLTableName());
					mHasBodyParseErrors = true;
				}
			}
			if (mQueryWhereCondition.length() == 0) {
				//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithFilter3 " + mCurrentTable.getSQLTableName());
				return;
			}
			mNewQuery.setWhereCondition(mQueryWhereCondition.toString());
			mNewQuery.setTableName(mCurrentTable.getSQLTableName());
			mNewQuery.setType(QueryType.qtUPDATE);
			//mNewQuery.dump();
			if (mNewQuery.IsReady()) {
				synchronized (mQueriesList) {
					mQueriesList.putQuery(mNewQuery);
				}
				mRecordsCount++;
			}
			mNewQuery = null;
		}
		catch (Exception e) {
			//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithFilter4 " + mCurrentTable.getSQLTableName());
			mHasBodyParseErrors = true;
			e.printStackTrace();
		}
	}
	private void CreateQueryByAttrsForInsert(Attributes atts) {
		//System.out.println("CreateQueryByAttrsForInsert");
		if (mCurrentTable == null) {
			//Log.v("sw",this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsForInsert");
			mHasBodyParseErrors = true;
			return;
		}
		int attsCount = atts.getLength();
		try {
			mNewQuery = new QueryInfo();
			TableField field = null;
			for (int i = 0; i < attsCount; i++) {
				field = mCurrentTable.getFields().get(atts.getLocalName(i));
				if (field != null) {
					switch (field.getType()) {
						case BLOB_TYPE:
							mNewQuery.getValues().put("[" + field.getName() + "]", ConvertToBlob(atts.getValue(i), field.getName()));
							break;
						case DOUBLE_TYPE:
							if (atts.getValue(i).length() != 0)
								mNewQuery.getValues().put("[" + field.getName() + "]", Double.parseDouble(atts.getValue(i)));
							break;
						default:
							mNewQuery.getValues().put("[" + field.getName() + "]", atts.getValue(i));
							break;
					}
				}
				else {
					//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsForInsert1 " + mCurrentTable.getSQLTableName());
					mHasBodyParseErrors = true;
				}
			}
			mNewQuery.setTableName(mCurrentTable.getSQLTableName());
			mNewQuery.setType(QueryType.qtINSERT);
			if (mNewQuery.IsReady()) {
				synchronized (mQueriesList) {
					mQueriesList.putQuery(mNewQuery);
				}
				mRecordsCount++;
			}
			mNewQuery = null;
		}
		catch (Exception e) {
			//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsForInsert2 " + mCurrentTable.getSQLTableName());
			mHasBodyParseErrors = true;
			e.printStackTrace();
		}
	}
	void addRememberedValues() {
		try {
			mNewQuery.getValues().putAll(preValues);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	void _parsePriceDataLine(Attributes atts) {
		//preValues.put("Vladelec", Hex.decodeHex(fakePriceVladelecKey));
		//CreateQueryByRefWithWhereCondition(atts, true);
	}
	void parseKartaKlientaDok() {
		//System.out.println("+++start parseKartaKlientaDok");
		mQueriesList.ExecuteQueries();
		String DeletionMark = allValues.child("DeletionMark").value.property.value();
		String Ref = allValues.child("Ref").value.property.value();
		String kmntr = allValues.child("Комментарий").value.property.value();
		String nzvn = allValues.child("Название").value.property.value();
		String Number = allValues.child("Number").value.property.value();
		String sql = "delete from KartaKlientaKlient2 where UIN='" + Ref + "';";
		mDB.execSQL(sql);
		sql = "delete from KartaKlientaNomenklatura2 where UIN='" + Ref + "';";
		mDB.execSQL(sql);
		sql = "delete from KartaKlientaDok2 where UIN='" + Ref + "';";
		mDB.execSQL(sql);
		if (!DeletionMark.trim().toUpperCase().equals("TRUE")) {
			sql = "insert into KartaKlientaDok2 (Nazvanie,Kommentarii,UIN,Number) values ("//
					+"'"+nzvn.replace('\'', '"')+"'"//
					+",'"+kmntr.replace('\'', '"')+"'"//
					+",'"+Ref.replace('\'', '"')+"'"//
					+",'"+Number.replace('\'', '"')+"'"//
					+");";
			mDB.execSQL(sql);
		}
		//System.out.println("+++done parseKartaKlientaDok");
	}
	void parseKartaKlientaKlient() {
		//System.out.println("start parseKartaKlientaKlient");
		String DeletionMark = allValues.child("DeletionMark").value.property.value();
		String Ref = allValues.child("Ref").value.property.value();
		String vladelec = allValues.child("Контрагент").value.property.value();
		String sql = "delete from KartaKlientaKlient2 where UIN='" + Ref + "' and vladelec=x'"+vladelec+"';";
		mDB.execSQL(sql);
		if (!DeletionMark.trim().toUpperCase().equals("TRUE")) {
			sql = "insert into KartaKlientaKlient2 (UIN,vladelec) values ("//
					+"'"+Ref.replace('\'', '"')+"'"//
					+",x'"+vladelec.replace('\'', '"')+"'"//
					+");";
			mDB.execSQL(sql);
		}
	}
	void parseKartaKlientaNomenklatura() {
		//System.out.println("start parseKartaKlientaNomenklatura");
		String DeletionMark = allValues.child("DeletionMark").value.property.value();
		String Ref = allValues.child("Ref").value.property.value();
		String tovar = allValues.child("Номенклатура").value.property.value();
		String sql = "delete from KartaKlientaNomenklatura2 where UIN='" + Ref + "' and tovar=x'"+tovar+"';";
		mDB.execSQL(sql);
		if (!DeletionMark.trim().toUpperCase().equals("TRUE")) {
			sql = "insert into KartaKlientaNomenklatura2 (UIN,tovar) values ("//
					+"'"+Ref.replace('\'', '"')+"'"//
					+",x'"+tovar.replace('\'', '"')+"'"//
					+");";
			mDB.execSQL(sql);
		}
	}
	void parseKartaKlientaKontragent(Attributes atts) {
		//System.out.println("start parseKartaKlientaKontragent");
		//System.out.println(allValues.dumpXML());
		//System.out.println("flush " + mQueriesList.getCount());
		mQueriesList.ExecuteQueries();
		String number = allValues.child("Number").value.property.value();
		String DeletionMark = allValues.child("DeletionMark").value.property.value();
		String Ref = allValues.child("Ref").value.property.value().replace('-', '0');
		String kontragent = allValues.child("Контрагент").value.property.value();
		//String nomenklatura=allValues.child("Номенклатура").value.property.value();
		//String dropSQL = "delete from KartaKlienta where number='" + specPriceNumber + "';";
		String dropSQL = "delete from KartaKlienta where number='" + number + "' and kontragent=x'" + kontragent + "';";
		mDB.execSQL(dropSQL);
		if (DeletionMark.trim().toUpperCase().equals("TRUE")) {
			//
		}
		else {
			String sql = "insert into KartaKlienta (_idrref,number,kontragent) values ("//
					+ "x'" + Ref + "'"//
					+ ",'" + number + "'"//
					+ ",x'" + kontragent + "'"//
					+ ");";
			//System.out.println("branch: " + sql);
			mDB.execSQL(sql);
		}
		//System.out.println("done parseKartaKlientaKontragent");
	}
	void parseKartaKlientaNomenklatura(Attributes atts) {
		//System.out.println("start parseKartaKlientaKontragent");
		//System.out.println(allValues.dumpXML());
		//System.out.println("flush " + mQueriesList.getCount());
		mQueriesList.ExecuteQueries();
		String number = allValues.child("Number").value.property.value();
		String DeletionMark = allValues.child("DeletionMark").value.property.value();
		String Ref = allValues.child("Ref").value.property.value().replace('-', '0');
		//String kontragent=allValues.child("Контрагент").value.property.value();
		String nomenklatura = allValues.child("Номенклатура").value.property.value();
		String dropSQL = "delete from KartaKlienta where number='" + number + "' and nomenklatura=x'" + nomenklatura + "';";
		mDB.execSQL(dropSQL);
		if (DeletionMark.trim().toUpperCase().equals("TRUE")) {
			//
		}
		else {
			String sql = "insert into KartaKlienta (_idrref,number,nomenklatura) values ("//
					+ "x'" + Ref + "'"//
					+ ",'" + number + "'"//
					+ ",x'" + nomenklatura + "'"//
					+ ");";
			//System.out.println("branch: " + sql);
			mDB.execSQL(sql);
		}
		//System.out.println("done parseKartaKlientaKontragent");
	}
	void parsePriceVladelecLine(Attributes atts) {
		//CreateQueryByRefWithWhereCondition( atts);
		//System.out.println("start parsePriceVladelecLine");
		String cuVladelec = atts.getValue("Владелец");
		//System.out.println("flush " + mQueriesList.getCount());
		mQueriesList.ExecuteQueries();

		if (specDeletionMark.trim().toUpperCase().equals("TRUE")) {
			String dropSQL = "delete from price where number='" + specPriceNumber + "';";
			//System.out.println("dead branch " + dropSQL);
			mDB.execSQL(dropSQL);
		}
		else {
			String sql = "delete from Price where Vladelec=x'" + cuVladelec + "'";
			//System.out.println("drop: " + sql);
			mDB.execSQL(sql);
			sql = "insert into price (_idrref,number,vladelec,nomenklatura,trafik) "//
					+ " select _idrref,number,X'" + cuVladelec + "' as vladelec,nomenklatura,trafik"//
					+ " from price where vladelec=x'" + fakePriceVladelecKey + "'";
			//System.out.println("branch: " + sql);
			mDB.execSQL(sql);
		}
		/*sql = "select count(*) as nn,vladelec, p.naimenovanie,r.number "//
			+ " from price r"// 
			+ " left join Podrazdeleniya p on r.vladelec=p._idrref"//
			+ " group by r.vladelec"//
			+ " order by r.number";
		System.out.println(Auxiliary.fromCursor(mDB.rawQuery(sql, null)).dumpXML());*/
		//System.out.println("done parsePriceVladelecLine");
	}
	void CreateQueryByRefWithWhereConditionOnly(Attributes atts) {
		_createQueryByRefWithWhereCondition(atts, false);
	}
	void CreateQueryByRefWithWhereConditionAndReplaceVladelec(Attributes atts) {
		_createQueryByRefWithWhereCondition(atts, true);
	}
	private void _createQueryByRefWithWhereCondition(Attributes atts, boolean replaceVladelecToFake) {
		//System.out.println("CreateQueryByRefWithWhereCondition");
		if (mCurrentTable == null) {
			//System.out.println("mCurrentTable==null");
			//Log.v("sw",this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithWhereCondition");
			mHasBodyParseErrors = true;
			return;
		}
		//System.out.println("mCurrentTable is "+mCurrentTable.getSQLTableName());
		int attsCount = atts.getLength();
		try {
			mNewQuery = new QueryInfo();
			mQueryWhereCondition = mQueryWhereCondition.delete(0, mQueryWhereCondition.length());
			String Nomenklatura = "00";
			String x = atts.getValue("Номенклатура");
			if (x != null) {
				Nomenklatura = "x'" + x + "'";
			}
			mQueryWhereCondition.append("[_IDRRef]=" + pre_IDDRef + " and Nomenklatura=" + Nomenklatura);
			mNewQuery.setWhereCondition(mQueryWhereCondition.toString());
			mNewQuery.setTableName(mCurrentTable.getSQLTableName());
			TableField field = null;
			for (int i = 0; i < attsCount; i++) {
				field = mCurrentTable.getFields().get(atts.getLocalName(i));
				if (field != null) {
					switch (field.getType()) {
						case BLOB_TYPE:
							mNewQuery.getValues().put("[" + field.getName() + "]", ConvertToBlob(atts.getValue(i), field.getName()));
							break;
						case DOUBLE_TYPE:
							if (atts.getValue(i).length() != 0)
								mNewQuery.getValues().put("[" + field.getName() + "]", Double.parseDouble(atts.getValue(i)));
							break;
						default:
							mNewQuery.getValues().put("[" + field.getName() + "]", atts.getValue(i));
							break;
					}
				}
			}
			addRememberedValues();
			if (replaceVladelecToFake) {
				mNewQuery.getValues().remove("[Vladelec]");
				mNewQuery.getValues().put("[Vladelec]", Hex.decodeHex(fakePriceVladelecKey));
			}
			/*if (replaceNumberToFake) {
				mNewQuery.getValues().remove("[number]");
				mNewQuery.getValues().put("[number]", fakePriceVladelecKey);
			}*/
			mNewQuery.setType(QueryType.qtUPDATE);
			//mNewQuery.dump();
			if (mNewQuery.IsReady()) {
				synchronized (mQueriesList) {
					mQueriesList.putQuery(mNewQuery);
				}
				mRecordsCount++;
			}
			//System.out.println("/ "+mCurrentTable.getSQLTableName());
			//mNewQuery.dump();
			mNewQuery = null;
		}
		catch (Exception e) {
			//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithWhereCondition3 " + mCurrentTable.getSQLTableName());
			mHasBodyParseErrors = true;
			e.printStackTrace();
		}
	}
	private void CreateQueryByAttrsWithWhereCondition(Attributes atts) {
		//System.out.println("CreateQueryByAttrsWithWhereCondition");
		if (mCurrentTable == null) {
			//System.out.println("mCurrentTable==null");
			//Log.v("sw",this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithWhereCondition");
			mHasBodyParseErrors = true;
			return;
		}
		//System.out.println("mCurrentTable is "+mCurrentTable.getSQLTableName());
		int attsCount = atts.getLength();
		try {
			mNewQuery = new QueryInfo();
			mQueryWhereCondition = mQueryWhereCondition.delete(0, mQueryWhereCondition.length());
			TableField field = null;
			for (int i = 0; i < attsCount; i++) {
				field = mCurrentTable.getFields().get(atts.getLocalName(i));
				if (field != null) {
					if (i == attsCount - 1) {//last attribute
						mQueryWhereCondition.append("[" + field.getName() + "]=" + ConvertForConditions(atts.getValue(i)) + " ");
					}
					else {
						mQueryWhereCondition.append("[" + field.getName() + "]=" + ConvertForConditions(atts.getValue(i)) + " AND ");
					}
					switch (field.getType()) {
						case BLOB_TYPE:
							mNewQuery.getValues().put("[" + field.getName() + "]", ConvertToBlob(atts.getValue(i), field.getName()));
							break;
						case DOUBLE_TYPE:
							if (atts.getValue(i).length() != 0)
								mNewQuery.getValues().put("[" + field.getName() + "]", Double.parseDouble(atts.getValue(i)));
							break;
						default:
							mNewQuery.getValues().put("[" + field.getName() + "]", atts.getValue(i));
							break;
					}
				}
				else {
					//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithWhereCondition1 " + mCurrentTable.getSQLTableName());
					mHasBodyParseErrors = true;
				}
			}
			//addRememberedValues();
			if (mQueryWhereCondition.length() == 0) {
				//System.out.println("mQueryWhereCondition.length() == 0");
				//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithWhereCondition2 " + mCurrentTable.getSQLTableName());
				return;
			}
			mNewQuery.setWhereCondition(mQueryWhereCondition.toString());
			mNewQuery.setTableName(mCurrentTable.getSQLTableName());
			mNewQuery.setType(QueryType.qtUPDATE);
			//mNewQuery.dump();
			if (mNewQuery.IsReady()) {
				synchronized (mQueriesList) {
					mQueriesList.putQuery(mNewQuery);
				}
				mRecordsCount++;
			}
			//System.out.println("/ "+mCurrentTable.getSQLTableName());
			//mNewQuery.dump();
			mNewQuery = null;
		}
		catch (Exception e) {
			//sweetlife.horeca.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateQueryByAttrsWithWhereCondition3 " + mCurrentTable.getSQLTableName());
			mHasBodyParseErrors = true;
			e.printStackTrace();
		}
	}
	private void CreateWhereCondition(Attributes atts) {
		mQueryWhereCondition = mQueryWhereCondition.delete(0, mQueryWhereCondition.length());
		TableField field = null;
		int attsCount = atts.getLength();
		for (int i = 0; i < attsCount; i++) {
			field = mCurrentTable.getFields().get(atts.getLocalName(i));
			if (field != null) {
				if (i == attsCount - 1) {//last attribute
					mQueryWhereCondition.append("[" + field.getName() + "]=" + ConvertForConditions(atts.getValue(i)) + " ");
				}
				else {
					mQueryWhereCondition.append("[" + field.getName() + "]=" + ConvertForConditions(atts.getValue(i)) + " AND ");
				}
			}
			else {
				sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE CreateWhereCondition " + mCurrentTable.getSQLTableName());
				mHasBodyParseErrors = true;
			}
		}
		if (mQueryWhereCondition.length() > 0) {
			mHasFilter = true;
		}
	}
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		//System.out.println("endElement " + localName + " /" + (mCurrentTable == null ? "null" : mCurrentTable.getSQLTableName()));
		if (path.size() > 0) {
			path.removeElementAt(path.size() - 1);
		}
		try {
			if (localName.equals("DocumentObject.Прайс")) {
				//System.out.println("end DocumentObject.Прайс - " + specDeletionMark);
				try {
					//if (specDeletionMark.trim().toUpperCase().equals("TRUE")) {
					String sql = "delete from Price where Vladelec=x'" + fakePriceVladelecKey + "'";
					//specVladelec;
					//System.out.println("end DocumentObject.Прайс - " + sql);
					mDB.execSQL(sql);
					//} else {
					//	System.out.println("end DocumentObject.Прайс - ok");
					//}
				}
				catch (Throwable t) {
					t.printStackTrace();
					//System.out.println("DocumentObject.Прайс - " + t);
				}
			}
			if (localName.equals("DocumentObject.КартаКлиента")) {
				try {
					String sql = "delete from KartaKlienta where number=" + fakePriceVladelecKey + "";
					//System.out.println("end DocumentObject.КартаКлиента - " + sql);
					mDB.execSQL(sql);
				}
				catch (Throwable t) {
					t.printStackTrace();
					//System.out.println("DocumentObject.КартаКлиента - " + t);
				}
			}
			if (mCurrentTable != null && mXMLTableName.compareTo(localName) == 0) {
				mXMLTableName = null;
				mCurrentTable = null;
				mQueryWhereCondition = mQueryWhereCondition.delete(0, mQueryWhereCondition.length());
			}
			else {
				if (NODE_RECORDS.compareTo(localName) == 0) {
					mHasRecorder = false;
					mHasFilter = false;
					mQueryWhereCondition = mQueryWhereCondition.delete(0, mQueryWhereCondition.length());
				}
				else {
					if (NODE_ASSORTIMENT.compareTo(localName) == 0//
							|| NODE_VLADELCY.compareTo(localName) == 0//
							|| NODE_TOVARY.compareTo(localName) == 0//
					) {
						mHasRecorder = false;
						mHasFilter = false;
						mQueryWhereCondition = mQueryWhereCondition.delete(0, mQueryWhereCondition.length());
					}
					else {
						if (NODE_HEADER.compareTo(qName) == 0) {
							mIsHeader = false;
							mCurrentHeaderNode = null;
							return;
						}
					}
				}
			}
		}
		catch (Exception e) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE endElement " + mCurrentTable.getSQLTableName());
			mHasBodyParseErrors = true;
			e.printStackTrace();
		}
	}
	@Override
	public void characters(char ch[], int start, int length) {
		try {
			String chars = new String(ch, start, length);
			chars = chars.trim();
			if (mIsHeader && chars.length() != 0) {
				if (mCurrentHeaderNode.equals(NODE_EXCHANGE_PLAN)) {
					mDeltaData.setExchangePlan(chars);
				}
				else
					if (mCurrentHeaderNode.equals(NODE_TO)) {
						mDeltaData.setTo(chars);
					}
					else
						if (mCurrentHeaderNode.equals(NODE_FROM)) {
							mDeltaData.setFrom(chars);
						}
						else
							if (mCurrentHeaderNode.equals(NODE_MESSAGE_NO)) {
								mDeltaData.setMessageNo(Integer.parseInt(chars));
							}
							else
								if (mCurrentHeaderNode.equals(NODE_RECEIVE_NO)) {
									mDeltaData.setReceivedNo(Integer.parseInt(chars));
								}
			}
		}
		catch (Exception e) {
			sweetlife.android10.log.LogHelper.debug(this.getClass().getCanonicalName() + "SWLIFE characters " + mCurrentTable.getSQLTableName());
			mHasHeaderParseErrors = true;
			e.printStackTrace();
		}
	}
	private byte[] ConvertToBlob(String value, String attribute) {
		if (value == null) {
			return null;
		}
		if (value.length() == 32) {
			return Hex.decodeHex(value);
		}
		else
			if (value.equalsIgnoreCase(STRING_TRUE)) {
				if (attribute.compareToIgnoreCase(IS_FOLDER) == 0) {
					//System.out.println(IS_FOLDER + "=" + BLOB_FALSE + " / " + value);
					return Hex.decodeHex(BLOB_FALSE);
				}
				return Hex.decodeHex(BLOB_TRUE);
			}
			else
				if (value.equalsIgnoreCase(STRING_FALSE)) {
					if (attribute.compareToIgnoreCase(IS_FOLDER) == 0) {
						//System.out.println(IS_FOLDER + "=" + BLOB_TRUE + " / " + value);
						return Hex.decodeHex(BLOB_TRUE);
					}
					return Hex.decodeHex(BLOB_FALSE);
				}
				else {
					return null;
				}
	}
	private String ConvertForConditions(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() == 32) {
			value = "x'" + value + "'";
		}
		else
			if (value.equalsIgnoreCase(STRING_TRUE)) {
				value = "x'01'";
			}
			else
				if (value.equalsIgnoreCase(STRING_FALSE)) {
					value = "x'00'";
				}
				else {
					value = "'" + value + "'";
				}
		return value;
	}
	public boolean HasHeaderParseErrors() {
		return mHasHeaderParseErrors;
	}
	public void setHasHeaderParseErrors(boolean hasHeaderParseErrors) {
		mHasHeaderParseErrors = hasHeaderParseErrors;
	}
	public boolean HasBodyParseErrors() {
		return mHasBodyParseErrors;
	}
	public void setHasBodyParseErrors(boolean hasBodyParseErrors) {
		mHasBodyParseErrors = hasBodyParseErrors;
	}
	public boolean IsComplete() {
		return mIsComplete;
	}
	public void setIsComplete(boolean isComplete) {
		mIsComplete = isComplete;
	}
	public DeltaData getDeltaData() {
		return mDeltaData;
	}
	public void setDeltaData(DeltaData deltaData) {
		mDeltaData = deltaData;
	}
	public QueriesList getQueriesList() {
		return mQueriesList;
	}
}