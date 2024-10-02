package sweetlife.android10.ui;

import java.io.File;
import java.text.*;
import java.util.*;

import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.Settings;
import sweetlife.android10.data.common.*;
import sweetlife.android10.data.orders.*;
import sweetlife.android10.database.Request_Bids;
import sweetlife.android10.database.nomenclature.*;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.*;
import sweetlife.android10.utils.AsyncTaskManager;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.ManagedAsyncTask;
import sweetlife.android10.utils.SystemHelper;

import android.app.*;
import android.content.DialogInterface;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import sweetlife.android10.*;
import sweetlife.android10.widgets.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.properties.*;
import tee.binding.task.*;

/*
I: msg Выгрузка (200, OK):
I: result
I: <>
I: 	<Статус>1</Статус>
I: 	<Сообщение></Сообщение>
I: 	<ДанныеПоЗаказам>
I: 		<ВнешнийНомер>HRC682-0829123639817</ВнешнийНомер>
I: 		<Статус>1</Статус>
I: 		<Сообщение></Сообщение>
I: 		<Заказы>
I: 			<Номер>12-1855056</Номер>
I: 			<Тип>Заказ покупателя</Тип>
I: 			<Статус>2</Статус>
I: 			<Сообщение></Сообщение>
I: 			<НеПодтвержденныеПозиции>
I: 				<Номенклатура>79580, Майонез Печагин 67%Professional ведро 940 мл/880 гр</Номенклатура>
I: 				<КоличествоЗаказано>24</КоличествоЗаказано>
I: 				<КоличествоДефицит>23</КоличествоДефицит>
I: 				<КоличествоПодтверждено>1</КоличествоПодтверждено>
I: 				<ДатаПоступления>20240829</ДатаПоступления>
I: 				<Текст>с 06/06 повышение на масло  5-10,5%</Текст>
I: 				<Аналоги>
I: 					<Артикул>79581</Артикул>
I: 					<Наименование>Майонез Печагин Экстра 67% ведро 3 л/2,82 кг</Наименование>
I: 					<ДоступноеКоличество>143</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>79805</Артикул>
I: 					<Наименование>Майонез Печагин оливковый 67% 940 мл/880 гр</Наименование>
I: 					<ДоступноеКоличество>60</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>83674</Артикул>
I: 					<Наименование>Майонез Печагин провансаль дой-пак 67% 420 мл/395 гр</Наименование>
I: 					<ДоступноеКоличество>154</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>83173</Артикул>
I: 					<Наименование>Майонез Печагин 56% Professional 930 мл/880 гр</Наименование>
I: 					<ДоступноеКоличество>30</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>110540</Артикул>
I: 					<Наименование>Майонез Efko Food Веганез 56% 3л</Наименование>
I: 					<ДоступноеКоличество>2</ДоступноеКоличество>
I: 				</Аналоги>
I: 			</НеПодтвержденныеПозиции>
I: 		</Заказы>
I: 	</ДанныеПоЗаказам>
I: 	<ДанныеПоЗаказам>
I: 		<ВнешнийНомер>HRC682-0829125920686</ВнешнийНомер>
I: 		<Статус>1</Статус>
I: 		<Сообщение></Сообщение>
I: 		<Заказы>
I: 			<Номер>12-1855058</Номер>
I: 			<Тип>Заказ покупателя</Тип>
I: 			<Статус>2</Статус>
I: 			<Сообщение></Сообщение>
I: 			<НеПодтвержденныеПозиции>
I: 				<Номенклатура>110540, Майонез Efko Food Веганез 56% 3л</Номенклатура>
I: 				<КоличествоЗаказано>24</КоличествоЗаказано>
I: 				<КоличествоДефицит>22</КоличествоДефицит>
I: 				<КоличествоПодтверждено>2</КоличествоПодтверждено>
I: 				<ДатаПоступления></ДатаПоступления>
I: 				<Текст></Текст>
I: 				<Аналоги>
I: 					<Артикул>79580</Артикул>
I: 					<Наименование>Майонез Печагин 67%Professional ведро 940 мл/880 гр</Наименование>
I: 					<ДоступноеКоличество>0</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>79581</Артикул>
I: 					<Наименование>Майонез Печагин Экстра 67% ведро 3 л/2,82 кг</Наименование>
I: 					<ДоступноеКоличество>143</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>79805</Артикул>
I: 					<Наименование>Майонез Печагин оливковый 67% 940 мл/880 гр</Наименование>
I: 					<ДоступноеКоличество>60</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>83173</Артикул>
I: 					<Наименование>Майонез Печагин 56% Professional 930 мл/880 гр</Наименование>
I: 					<ДоступноеКоличество>30</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>116235</Артикул>
I: 					<Наименование>Майонез Юг Profi Провансаль 67% 5л</Наименование>
I: 					<ДоступноеКоличество>82</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>116661</Артикул>
I: 					<Наименование>Майонез Pechagin Professional Profi 67% 5л Ведро</Наименование>
I: 					<ДоступноеКоличество>130</ДоступноеКоличество>
I: 				</Аналоги>
I: 				<Аналоги>
I: 					<Артикул>110535</Артикул>
I: 					<Наименование>Майонез EFKO FOOD Professional универсальный 67% 10л/9,34кг</Наименование>
I: 					<ДоступноеКоличество>83</ДоступноеКоличество>
I: 				</Аналоги>
I: 			</НеПодтвержденныеПозиции>
I: 		</Заказы>
I: 	</ДанныеПоЗаказам>
I: </>
*/
public class UploadOrderResult extends Activity{
	Layoutless layoutless;
	public static int UploadDialogResult = 836353523;
	DataGrid2 dataGrid;
	ColumnDescription singlecolumn;
	Bough data = null;

	public static void startUploadResultDialog(Context from,Bough parsed){
		Intent intent = new Intent();
		intent.setClass(from, UploadOrderResult.class);
		intent.putExtra("xml", parsed.dumpXML());
		from.startActivity(intent);
	}
	public static void startUploadResultDialog(Context from,String jsonData){
		Intent intent = new Intent();
		intent.setClass(from, UploadOrderResult.class);
		intent.putExtra("json", jsonData);
		from.startActivity(intent);
	}


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		String json = Auxiliary.activityExatras(this).child("json").value.property.value();
		String xml = Auxiliary.activityExatras(this).child("xml").value.property.value();
		//
		 //System.out.println("raw " + raw);
		//this.data = Bough.parseXML(raw);
		if(xml.trim().length()>1){
			System.out.println("UploadOrderResult xml " + xml);
			this.data = Bough.parseXML(xml);
		}else{
			System.out.println("UploadOrderResult json " + json);
			this.data = Bough.parseJSON(json);
		}

		createGUI();
		fillData();
	}

	void createGUI(){
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		setTitle("Результат отправки заказов");
		dataGrid = new DataGrid2(this);
		singlecolumn = new ColumnDescription();
		layoutless.child(dataGrid.noHead.is(true)
				.columns(new Column[]{
						singlecolumn.width.is(layoutless.width().property)
				})
				.left().is(0)
				.top().is(0)
				.width().is(layoutless.width().property)
				.height().is(layoutless.height().property)
		);
	}

	void fillData(){
		System.out.println("UploadOrderResult " + data.dumpXML());
		singlecolumn.clear();
		final String status = "Сообщение: ";//"Статус: " + data.child("Статус").value.property.value();
		final String soob = data.child("Сообщение").value.property.value();
		singlecolumn.cell(status, new Task(){
			public void doTask(){
				Auxiliary.warn(status + "\n\n" + soob, UploadOrderResult.this);
			}
		}, soob);
		Vector<Bough> danniypozakazam = data.children("ДанныеПоЗаказам");
		for(int ii = 0; ii < danniypozakazam.size(); ii++){
			Bough danniypozakazam1 = danniypozakazam.get(ii);
			addDanniePoZakazam(danniypozakazam1);
		}

		dataGrid.refresh();
	}

	void addDanniePoZakazam(Bough danniypozakazam1){

		final String status = "№: " + danniypozakazam1.child("ВнешнийНомер").value.property.value();// + ", статус: " + danniypozakazam1.child("Статус").value.property.value();
		final String soob = "Сообщение: " + danniypozakazam1.child("Сообщение").value.property.value();
		singlecolumn.cell(status, 0x33000000, new Task(){
			public void doTask(){
				Auxiliary.warn(status + "\n\n" + soob, UploadOrderResult.this);
			}
		}, soob);
		Vector<Bough> zakazy = danniypozakazam1.children("Заказы");
		for(int ii = 0; ii < zakazy.size(); ii++){
			Bough zakaz = zakazy.get(ii);
			addZakaz(zakaz);
		}
	}

	void addZakaz(Bough zakaz){
		final String status = zakaz.child("Тип").value.property.value() + " " + zakaz.child("Номер").value.property.value();// + ", статус: " + zakaz.child("Статус").value.property.value();
		final String soob = "Сообщение: " + zakaz.child("Сообщение").value.property.value();
		singlecolumn.cell(status, 0x22000000, new Task(){
			public void doTask(){
				Auxiliary.warn(status + "\n\n" + soob, UploadOrderResult.this);
			}
		}, soob);
		Vector<Bough> nepodpos = zakaz.children("НеПодтвержденныеПозиции");
		//if(nepodpos.size()>0){
		DateFormat to = new SimpleDateFormat("yyyy-MM-dd");
		for(int ii = 0; ii < nepodpos.size(); ii++){
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 1);
			String documentDate=to.format(c.getTime());
			String	documentNumber=zakaz.child("Номер").value.property.value();
			addNepodtverjdenie(nepodpos.get(ii),documentNumber,documentDate);
		}
		//}
	}

	void addNepodtverjdenie(Bough nepodpos,final String documentNumber,final String documentDate){
		String nomenklatura = nepodpos.child("Номенклатура").value.property.value();
		String zakazano = nepodpos.child("КоличествоЗаказано").value.property.value();
		String deficit = nepodpos.child("КоличествоДефицит").value.property.value();
		String podtver = nepodpos.child("КоличествоПодтверждено").value.property.value();
		String datapostup = Auxiliary.tryReFormatDate(nepodpos.child("ДатаПоступления").value.property.value(), "yyyyMMdd", "dd.MM.yyyy");
		String text = nepodpos.child("Текст").value.property.value();
		final String status = "не подтверждён " + nomenklatura + " - " + text;
		final String soob = "подтверждено " + podtver + " из " + zakazano + ", в дефиците " + deficit + ", поступление " + datapostup;
		singlecolumn.cell(status, 0x11000000, new Task(){
			public void doTask(){
				Auxiliary.warn(status + "\n\n" + soob, UploadOrderResult.this);
			}
		}, soob);
		Vector<Bough> analogi = nepodpos.children("Аналоги");
		for(int ii = 0; ii < analogi.size(); ii++){
			addAnalog(analogi.get(ii),documentNumber,documentDate);
		}
	}

	void addAnalog(Bough analog,final String documentNumber,final String documentDate){
		String art = analog.child("Артикул").value.property.value();
		String naimenov = analog.child("Наименование").value.property.value();
		String kolvo = analog.child("ДоступноеКоличество").value.property.value();
		//final String status ="не подтверждён "+nomenklatura+" - "+text;
		//final String soob = "подтверждено " +podtver+" из "+zakazano+", в дефиците "+deficit+", поступление "+datapostup;
		singlecolumn.cell("заменить на аналог арт." + art + ", всего доступно " + kolvo, new Task(){
			public void doTask(){

				promptAnalog(art,documentNumber,documentDate);
			}
		}, ""+naimenov);
	}
	void promptAnalog(String art,String documentNumber,String documentDate){
		//Auxiliary.warn(art+", "+documentNumber+", "+documentDate, UploadOrderResult.this);
		//documentNumber="12-1854443";
		//documentDate="29.09.2024";
		Calendar c=Calendar.getInstance();
		Numeric curDate=new Numeric();
				try{
					curDate.value((int)Auxiliary.ddMMyyyy.parse(documentDate).getTime());
				}catch(Throwable t){
					t.printStackTrace();
				}
		Task task=new Task(){
			public void doTask(){
				EditOrderViaWeb.requestItemsChangeNew(documentNumber,documentDate,UploadOrderResult.this,new Task(){
					public  void doTask(){
						System.out.println("done newCreateZakazAnalog");
					}
				},null);
			}
		};
		Auxiliary.pickDate(UploadOrderResult.this,c,curDate,task);
	}
	/*
	void startNewAnalog(String art,String documentNumber,String documentDate){
		EditOrderViaWeb.requestItemsChangeNew(documentNumber,documentDate,UploadOrderResult.this,new Task(){
			public  void doTask(){
				System.out.println("done newCreateZakazAnalog");
			}
		},null);
	}*/
}
