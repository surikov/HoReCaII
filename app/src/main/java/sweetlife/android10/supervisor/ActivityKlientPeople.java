
package sweetlife.android10.supervisor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.*;
import java.util.*;
import java.util.Date;
import java.util.TimeZone;

import sweetlife.android10.consts.*;
import reactive.ui.*;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import tee.binding.Bough;
import tee.binding.it.Note;
import tee.binding.it.Numeric;
import tee.binding.it.Toggle;
import tee.binding.task.Task;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityKlientPeople extends Activity implements ITableColumnsNames {

	int productPageSize = 30;
	Numeric productOffset = new Numeric();

	static final int GET_GALLERY_PICTURE_1 = 121939394;
	static final int GET_GALLERY_PICTURE_2 = 223443434;
	static final int GET_GALLERY_PICTURE_3 = 988898787;
	Note noviAdres = new Note();
	Note komment = new Note();
	Note fileAbsPath_1 = new Note();
	Note fileAbsPath_2 = new Note();
	Note fileAbsPath_3 = new Note();

	String rol00004 = "Бухгалтер";
	String rol00002 = "Директор";
	String rol00006 = "Для печати актов бонусов";
	String rol00001 = "Закупщик";
	String rol00005 = "Собственник";
	String rol00003 = "Шеф-повар";
	String rol00008 = "Приемщик";

	String pochta = "";
	//String vremyaRaboty = "";
	//String vremyaRabotyS = "";
	//String vremyaRabotyPo = "";
	Numeric[] fromTime = new Numeric[]{new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric()};
	Numeric[] toTime = new Numeric[]{new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric(), new Numeric()};

	String oldAdresDostavki = "";

	//int platelshikNDS = 1;

	Note vremyaText = new Note();

	Note textPochta = new Note();
	Note textVremya = new Note();
	Note textInn = new Note();
	Note textKPP = new Note();
	Note textBic = new Note();
	Note textRC = new Note();
	Note textAdrFact = new Note();
	Note textAdrDost = new Note();
	Note textAdrUr = new Note();
	Note textOsnovKlient = new Note();
	Note textNDS = new Note();
	Note valNDS = new Note();
	Note osobennosti = new Note();
	Note KommentariiPropusk = new Note();
	Note osobennostiRejimaRaboty = new Note();

	Toggle klientRabotayetBezPechati2 = new Toggle();
	Note textKlientRabotayetBezPechati2 = new Note();

	Layoutless layoutless;
	DataGrid itemsGrid;
	ColumnDescription columnItemNaimenovanie = new ColumnDescription();

	Vector<Bough> rows = new Vector<Bough>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		createGUI();
	}


	void createGUI() {
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		this.setTitle(ApplicationHoreca.getInstance().getClientInfo().getKod() + ": " + ApplicationHoreca.getInstance().getClientInfo().getName());
		itemsGrid = new DataGrid(this).center.is(true)//
				.pageSize.is(999)//itemsGridPageSize)//
		//.dataOffset.is(itemsGridOffset)//
				           /* .beforeFlip.is(new Task() {
					@Override
					public void doTask() {
						requeryItemGridData();
						flipItemsGrid();
					}
				})*/;
		layoutless.child(itemsGrid//
				//.noHead.is(true)//
				.columns(new Column[]{ //
						columnItemNaimenovanie.title.is("Контактные лица").width.is(layoutless.width().property.divide(2)) //
				})//
				.left().is(layoutless.width().property.divide(2))
				.top().is(0 * Auxiliary.tapSize)//
				.width().is(layoutless.width().property.divide(2))//
				.height().is(layoutless.height().property.minus(Auxiliary.tapSize))//
		);

		layoutless.child(new Decor(this).labelText.is(textPochta) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 0.5)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(textVremya) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 1.0)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 1)//
		);
		layoutless.child(new Decor(this).labelText.is(textInn) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 1.5)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(textKPP) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 2.0)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(textBic) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 2.5)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(textRC) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 3.0)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(textAdrFact) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 3.5)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 1)//
		);
		layoutless.child(new Decor(this).labelText.is(textAdrDost) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 4.0)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 1)//
		);
		layoutless.child(new Decor(this).labelText.is(textAdrUr) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 4.5)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 1)//
		);
		layoutless.child(new Decor(this).labelText.is(textOsnovKlient) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 5.0)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(textNDS) //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 5.5)//
				.width().is(layoutless.width().property.divide(2).minus(Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
/*
		layoutless.child(new Decor(this).labelText.is("Работает без печати: ") //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 8.0)//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(textKlientRabotayetBezPechati) //
				.left().is(Auxiliary.tapSize * 3.2)//
				.top().is(Auxiliary.tapSize * 8.0)//
				.width().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
*/
		layoutless.child(new Decor(this).labelText.is("Особенности: ") //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 6.0)//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(osobennosti) //
				.left().is(Auxiliary.tapSize * 3)//
				.top().is(Auxiliary.tapSize * 6.0)//
				.width().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);

		layoutless.child(new Decor(this).labelText.is("Комм. к проп.: ") //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 7.0)//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(this.KommentariiPropusk) //
				.left().is(Auxiliary.tapSize * 3)//
				.top().is(Auxiliary.tapSize * 7.0)//
				.width().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);

		layoutless.child(new Decor(this).labelText.is("Особенности реж.: ") //
				.left().is(Auxiliary.tapSize * 0.5)//
				.top().is(Auxiliary.tapSize * 8.0)//
				.width().is(3 * Auxiliary.tapSize)//
				.height().is(Auxiliary.tapSize * 0.5)//
		);
		layoutless.child(new Decor(this).labelText.is(this.osobennostiRejimaRaboty) //
				.left().is(Auxiliary.tapSize * 3)//
				.top().is(Auxiliary.tapSize * 8.0)//
				.width().is(layoutless.width().property.divide(2).minus(2.5 * Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize * 0.5)//
		);

        /*layoutless.child(new Knob(this).labelText.is("Выгрузить").afterTap.is(new Task() {
                    @Override
                    public void doTask() {
                        //promptVigruzit();
                    }
                })//
                        .left().is(0)//
                        .top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
                        .width().is(layoutless.width().property.divide(2))//
                        .height().is(Auxiliary.tapSize)//
        );*/
		layoutless.child(new Knob(this).labelText.is("Добавить контакт").afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptDobavit(null, false, 0, null, null, 0, null, null);
					}
				})//
						.left().is(layoutless.width().property.minus(4 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.width().is(Auxiliary.tapSize * 4)//
						.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).labelText.is("Смена адр. дост.").afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptAdresDostavki();
					}
				})//
						.left().is(layoutless.width().property.minus(8 * Auxiliary.tapSize))//
						.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.width().is(Auxiliary.tapSize * 4)//
						.height().is(Auxiliary.tapSize)//
		);
		layoutless.child(new Knob(this).labelText.is("Изменить").afterTap.is(new Task() {
					@Override
					public void doTask() {
						promptInfo();
					}
				})//
						.left().is(0)//
						.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
						.width().is(Auxiliary.tapSize * 4)//
						.height().is(Auxiliary.tapSize)//
		);
		/*
		layoutless.child(new Decor(this).labelText.is(vremyaText).labelAlignRightCenter()
				.left().is(4 * Auxiliary.tapSize)//
				.top().is(layoutless.height().property.minus(Auxiliary.tapSize))//
				.width().is(layoutless.width().property.minus(8 * Auxiliary.tapSize))//
				.height().is(Auxiliary.tapSize)//
		);*/
		loadData();
	}

	void promptInfo() {
		final Note email = new Note();
		//final Note vremya = new Note();
		/*
		final Numeric vremS = new Numeric();
		final Numeric vremPo = new Numeric();

		final Numeric from1 = new Numeric();
		final Numeric to1 = new Numeric();
		final Numeric from2 = new Numeric();
		final Numeric to2 = new Numeric();
		final Numeric from3 = new Numeric();
		final Numeric to3 = new Numeric();
		final Numeric from4 = new Numeric();
		final Numeric to4 = new Numeric();
		final Numeric from5 = new Numeric();
		final Numeric to5 = new Numeric();
		final Numeric from6 = new Numeric();
		final Numeric to6 = new Numeric();
*/

		email.value(this.pochta);
		//vremya.value(this.vremyaRaboty);
		try {
			SimpleDateFormat fromDate = new SimpleDateFormat("yyyyMMddHHmmss");
			fromDate.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			//vremS.value((double) fromDate.parse(this.vremyaRabotyS).getTime());
			//vremPo.value((double) fromDate.parse(this.vremyaRabotyPo).getTime());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		final Numeric ndsSelect = new Numeric();
		if (valNDS.value().equals("-1")) ndsSelect.value(0);
		if (valNDS.value().equals("0")) ndsSelect.value(0);
		if (valNDS.value().equals("1")) ndsSelect.value(1);
		Auxiliary.pick(this, "", new SubLayoutless(this)//
						.child(new Decor(this).labelText.is("e-mail").labelAlignRightTop().top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(email).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 0.25).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("особенности клиента").labelAlignRightTop().top().is(Auxiliary.tapSize * 1.25).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(this.osobennosti).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 1.0).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.75))//


						.child(new Decor(this).labelText.is("особенности режима").labelAlignRightTop().top().is(Auxiliary.tapSize * 2.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(this.osobennostiRejimaRaboty).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 1.75).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.75))//

						.child(new Decor(this).labelText.is("комментарии к пропуску").labelAlignRightTop().top().is(Auxiliary.tapSize * 2.75).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(this.KommentariiPropusk).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 0.75))//


						.child(new Decor(this).labelText.is("время работы").labelAlignRightTop().top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(fromTime[0]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 3.25).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[0]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 3.25).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[0].value(0);
								toTime[0].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 3.25).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("пн").labelAlignRightTop().top().is(Auxiliary.tapSize * 4.25).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactTime(this).time.is(fromTime[1]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 4.0).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[1]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 4.0).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[1].value(0);
								toTime[1].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 4.0).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("вт").labelAlignRightTop().top().is(Auxiliary.tapSize * 5.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactTime(this).time.is(fromTime[2]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 4.75).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[2]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 4.75).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[2].value(0);
								toTime[2].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 4.75).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("ср").labelAlignRightTop().top().is(Auxiliary.tapSize * 5.75).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactTime(this).time.is(fromTime[3]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 5.5).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[3]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 5.5).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[3].value(0);
								toTime[3].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 5.5).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("чт").labelAlignRightTop().top().is(Auxiliary.tapSize * 6.5).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactTime(this).time.is(fromTime[4]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 6.25).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[4]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 6.25).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[4].value(0);
								toTime[4].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 6.25).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("пт").labelAlignRightTop().top().is(Auxiliary.tapSize * 7.25).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactTime(this).time.is(fromTime[5]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 7.0).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[5]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 7.0).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[5].value(0);
								toTime[5].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 7.0).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("сб").labelAlignRightTop().top().is(Auxiliary.tapSize * 8.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactTime(this).time.is(fromTime[6]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 7.75).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[6]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 7.75).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[6].value(0);
								toTime[6].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 7.75).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("вс").labelAlignRightTop().top().is(Auxiliary.tapSize * 8.75).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactTime(this).time.is(fromTime[7]).format.is("HH:mm").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 8.5).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactTime(this).time.is(toTime[7]).format.is("HH:mm").left().is(Auxiliary.tapSize * 6.5).top().is(Auxiliary.tapSize * 8.5).width().is(Auxiliary.tapSize * 2.5).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).labelText.is("X").afterTap.is(new Task() {
							public void doTask() {
								fromTime[7].value(0);
								toTime[7].value(0);
							}
						}).left().is(Auxiliary.tapSize * 9.0).top().is(Auxiliary.tapSize * 8.5).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 0.75))//

						/*.child(new RedactSingleChoice(this)
								//.item("?")
								.item("нет")
								.item("да")
								.selection.is(ndsSelect)
								.left().is(Auxiliary.tapSize * 4.0)
								.top().is(Auxiliary.tapSize * 3.5)
								.width().is(Auxiliary.tapSize * 5)
								.height().is(Auxiliary.tapSize * 1))//
*/
						.width().is(Auxiliary.tapSize * 12)//
						.height().is(Auxiliary.tapSize * 10.5)//
				, "Сохранить", new Task() {
					@Override
					public void doTask() {
						pochta = email.value();
						//vremyaRaboty=vremya.value();


						SimpleDateFormat toDate = new SimpleDateFormat("yyyyMMddHHmmss");
						toDate.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
						//vremyaRabotyS = toDate.format(new Date(new Double(vremS.value()).longValue()));
						//vremyaRabotyPo = toDate.format(new Date(new Double(vremPo.value()).longValue()));
						if (ndsSelect.value() == 0) valNDS.value("0");
						if (ndsSelect.value() == 1) valNDS.value("1");
						if (valNDS.value().equals("-1"))
							ActivityKlientPeople.this.textNDS.value("Плательщик НДС: -");
						if (valNDS.value().equals("0"))
							ActivityKlientPeople.this.textNDS.value("Плательщик НДС: нет");
						if (valNDS.value().equals("1"))
							ActivityKlientPeople.this.textNDS.value("Плательщик НДС: да");
						//if (ndsSelect.value() == 2) valNDS.value("1");
/*
                        System.out.println(email.value());
                        System.out.println(vremya.value());
                        System.out.println(toDate.format(new Date(new Double(vremS.value()).longValue())));
                        System.out.println(toDate.format(new Date(new Double(vremPo.value()).longValue())));
*/
						sendDelete("", new Task() {
							public void doTask() {
								loadData();
							}
						});

					}
				}, null, null, null, null);
	}

	void sendAdresDostavki() {//String noviy, String komment) {
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaSmenuAdresaKlienta";
		System.out.println("sendAdresDostavki url is " + url);
		String sql = "select pdr.kod as podrkod from Kontragenty kk join Podrazdeleniya pdr on pdr._idrref=kk.podrazdelenie where kk.kod=\"" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "\";";
		String podrazdKod = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null)).child("row").child("podrkod").value.property.value();
		final String body = "{"
				+ "\"АдресДоставки\":\"" + ActivityCheckList2.safe(noviAdres.value()) + "\""
				+ ",\"Контрагент\":\"" + ApplicationHoreca.getInstance().getClientInfo().getKod() + "\""
				+ ",\"Ответственный\":\"" + Cfg.whoCheckListOwner() + "\""
				+ ",\"Комментарий\":\"" + ActivityCheckList2.safe(komment.value()) + "\""
				+ ",\"Подразделение\":\"" + podrazdKod + "\""
				+ "}";
		System.out.println("sendAdresDostavki body is " + body);
		final Note status = new Note();
		final Note message = new Note();
		final Note numDoc = new Note();
		Expect expect = new Expect().status.is("Отправка заявки").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					Bough result = Auxiliary.loadTextFromPrivatePOST(url, body.getBytes("UTF-8"), 60 * 1000
							, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword()
							, true
					);
					System.out.println("sendAdresDostavki result is " + result.dumpXML());
					Bough raw = Bough.parseJSON(result.child("raw").value.property.value());
					System.out.println("raw is " + raw.dumpXML());
					status.value(raw.child("Статус").value.property.value());
					message.value(raw.child("Сообщение").value.property.value());
					numDoc.value(raw.child("НомерЗаявки").value.property.value());
					sendFileByNum(numDoc.value(), message, fileAbsPath_1);
					sendFileByNum(numDoc.value(), message, fileAbsPath_2);
					sendFileByNum(numDoc.value(), message, fileAbsPath_3);
				} catch (Throwable t) {
					t.printStackTrace();
					message.value(t.getMessage());
				}
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				/*if (status.value().equals("0")) {
					sendFileByNum(numDoc.value(), message, fileAbsPath_1);
				} else {
					Auxiliary.warn(message.value(), ActivityKlientPeople.this);
				}*/
				Auxiliary.warn(message.value(), ActivityKlientPeople.this);
			}
		});
		expect.start(this);
	}
	void sendFileByNum(String numdoc, final Note message, Note fileAbsPath) {
		if (fileAbsPath.value().length() > 2) {
			String rashirenie = fileAbsPath.value().substring(fileAbsPath.value().lastIndexOf(".") + 1);
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaSmenuAdresaKlienta/" + numdoc + "?rash=" + rashirenie;
			System.out.println("sendFileByNum " + url);
			final Bough result = new Bough();
			try {
				File iofile = new File(fileAbsPath.value());
				int length = (int) iofile.length();
				final byte[] bytes = new byte[length];
				FileInputStream fileInputStream = new FileInputStream(iofile);
				DataInputStream dataInputStream = new DataInputStream(fileInputStream);
				dataInputStream.readFully(bytes);
				dataInputStream.close();
				System.out.println("bytes.length " + bytes.length+" /"+fileAbsPath.value());
				Bough b = Auxiliary.loadTextFromPrivatePOST(url, bytes, 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), false);
				message.value(message.value() + "\n" + "Файл в заявке ("+fileAbsPath.value()+"): " + b.child("raw").value.property.value());
			} catch (Throwable t) {
				message.value(message.value() + "\n" + t.getMessage());
				Auxiliary.warn(message.value(), ActivityKlientPeople.this);
			}
		}
	}
	void sendFileByNum222(String numdoc, final Note message, Note fileAbsPath) {
		if (fileAbsPath.value().length() > 2) {
			String rashirenie = fileAbsPath.value().substring(fileAbsPath.value().lastIndexOf(".") + 1);
			final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ZayavkaNaSmenuAdresaKlienta/" + numdoc + "?rash=" + rashirenie;
			System.out.println("sendFileByNum " + url);
			final Bough result = new Bough();
			try {
				File iofile = new File(fileAbsPath.value());
				int length = (int) iofile.length();
				final byte[] bytes = new byte[length];
				FileInputStream fileInputStream = new FileInputStream(iofile);
				DataInputStream dataInputStream = new DataInputStream(fileInputStream);
				dataInputStream.readFully(bytes);
				dataInputStream.close();
				System.out.println("bytes.length " + bytes.length);
				final Bough raw = new Bough();
				new Expect().task.is(new Task() {
					@Override
					public void doTask() {
						try {
							Bough b = Auxiliary.loadTextFromPrivatePOST(url, bytes, 33000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword(), false);
							System.out.println(b.dumpXML());
							//result.child("raw").value.is(b.child("raw").value.property.value());
							//result.child("message").value.is(b.child("message").value.property.value());
							message.value(message.value() + "\n" + "Файл в заявке: " + b.child("raw").value.property.value());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).afterDone.is(new Task() {
					@Override
					public void doTask() {
						//if (result.child("raw").value.property.value().equals("Всё ОК")) {
						//	saveFile(filePath);
						//}
						//Auxiliary.warn(result.child("message").value.property.value()+": "+result.child("raw").value.property.value(),ActivityLimitEdit.this);
						Auxiliary.warn(message.value(), ActivityKlientPeople.this);
					}
				}).status.is("Отправка файла").start(this);
			} catch (Throwable t) {
				//Auxiliary.warn(t.getMessage(),this);
				message.value(message.value() + "\n" + t.getMessage());
				Auxiliary.warn(message.value(), ActivityKlientPeople.this);
			}
		} else {
			Auxiliary.warn(message.value(), ActivityKlientPeople.this);
		}
	}

	void promptAdresDostavki() {
		/*final Note noviAdres = new Note().value(this.oldAdresDostavki);
		final Note komment = new Note();
		final Note filePath = new Note();*/
		noviAdres.value(this.oldAdresDostavki);
		Auxiliary.pick(this, "Заявка на смену адреса доставки", new SubLayoutless(this)//
						.child(new Decor(this).labelText.is("новый адрес доставки").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.25).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(noviAdres).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 1).width().is(Auxiliary.tapSize * 8).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Decor(this).labelText.is("комментарий к заявке").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.25).top().is(Auxiliary.tapSize * 2).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(komment).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 8).height().is(Auxiliary.tapSize * 0.75))//

						.child(new Decor(this).labelText.is("файл 1").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.25).top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(fileAbsPath_1).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 4).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).afterTap.is(new Task() {
									public void doTask() {
										Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
										intent.setType("*/*");
										intent.addCategory(Intent.CATEGORY_OPENABLE);
										intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
										Intent ch = Intent.createChooser(intent, "Выбор 1");
										startActivityForResult(ch, GET_GALLERY_PICTURE_1);
									}
								}).labelText.is("...").top().is(Auxiliary.tapSize * 3.75).left().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 1)
						)
						.child(new Decor(this).labelText.is("файл 2").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.25).top().is(Auxiliary.tapSize * 5).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(fileAbsPath_2).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 5.5).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).afterTap.is(new Task() {
									public void doTask() {
										Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
										intent.setType("*/*");
										intent.addCategory(Intent.CATEGORY_OPENABLE);
										intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
										Intent ch = Intent.createChooser(intent, "Выбор 2");
										startActivityForResult(ch, GET_GALLERY_PICTURE_2);
									}
								}).labelText.is("...").top().is(Auxiliary.tapSize * 5.25).left().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 1)
						)
						.child(new Decor(this).labelText.is("файл 3").labelAlignLeftTop().left().is(Auxiliary.tapSize * 0.25).top().is(Auxiliary.tapSize * 6.5).width().is(Auxiliary.tapSize * 3).height().is(Auxiliary.tapSize * 0.75))//
						.child(new RedactText(this).text.is(fileAbsPath_3).left().is(Auxiliary.tapSize * 0.5).top().is(Auxiliary.tapSize * 7).width().is(Auxiliary.tapSize * 7).height().is(Auxiliary.tapSize * 0.75))//
						.child(new Knob(this).afterTap.is(new Task() {
									public void doTask() {
										Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
										intent.setType("*/*");
										intent.addCategory(Intent.CATEGORY_OPENABLE);
										intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
										Intent ch = Intent.createChooser(intent, "Выбор 3");
										startActivityForResult(ch, GET_GALLERY_PICTURE_3);
									}
								}).labelText.is("...").top().is(Auxiliary.tapSize * 6.75).left().is(Auxiliary.tapSize * 7.5).width().is(Auxiliary.tapSize * 1).height().is(Auxiliary.tapSize * 1)
						)
						.width().is(Auxiliary.tapSize * 9)//
						.height().is(Auxiliary.tapSize * 11)//


				, "Отправить", new Task() {
					@Override
					public void doTask() {
						sendAdresDostavki();//noviAdres.value(), komment.value());
					}
				}, null, null, null, null);
	}


	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("onActivityResult " + requestCode + "/" + resultCode + "/" + data);

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case GET_GALLERY_PICTURE_1:
					String filePath1 = null;
					Uri uri1 = data.getData();
					filePath1 = Auxiliary.pathForMediaURI(this, uri1);
					fileAbsPath_1.value(filePath1);
					break;
				case GET_GALLERY_PICTURE_2:
					String filePath2 = null;
					Uri uri2 = data.getData();
					filePath2 = Auxiliary.pathForMediaURI(this, uri2);
					fileAbsPath_2.value(filePath2);
					break;
				case GET_GALLERY_PICTURE_3:
					String filePath3 = null;
					Uri uri3 = data.getData();
					filePath3 = Auxiliary.pathForMediaURI(this, uri3);
					fileAbsPath_3.value(filePath3);
					break;
			}
		}
	}

	void promptDobavit(final String kod, boolean _main, int _rolNum, String _doljnost, final String _fio, long _datarojd, String _mobtel, String _gortel) {

		String firstLabel = null;
		Task firstTask = null;
		String secondLabel = null;
		Task secondTask = null;

		final Toggle main = new Toggle();
		//final Note kontakntnoeLico = new Note();
		final Numeric rolNum = new Numeric();
		final Note doljnost = new Note();
		final Note fio = new Note();
		final Numeric datarojd = new Numeric();
		final Note mobtel = new Note();
		final Note gortel = new Note();


		if (kod == null) {
			firstLabel = "Добавить";
			firstTask = new Task() {
				@Override
				public void doTask() {
					String dastring = "";
					if (datarojd.value().longValue() > 0) {
						Date d = new Date(datarojd.value().longValue());
						dastring = Auxiliary.short1cDate.format(d);
					}
					final String s = dastring;
					sendAdd(fio.value(), rolByNum(rolNum.value().intValue()), doljnost.value(), fio.value(), s, main.value(), mobtel.value(), gortel.value()
							, new Task() {
								public void doTask() {
									loadData();
								}
							}
					);
				}
			};
		} else {
			main.value(_main);
			//final Note kontakntnoeLico = new Note();
			rolNum.value(_rolNum);
			doljnost.value(_doljnost);
			fio.value(_fio);
			datarojd.value((float) _datarojd);
			mobtel.value(_mobtel);
			gortel.value(_gortel);
			firstLabel = "Изменить";
			firstTask = new Task() {
				@Override
				public void doTask() {
					sendDelete(kod, new Task() {
						public void doTask() {
							String dastring = "";
							if (datarojd.value().longValue() > 0) {
								Date d = new Date(datarojd.value().longValue());
								dastring = Auxiliary.short1cDate.format(d);
							}
							final String s = dastring;
							sendAdd(fio.value(), rolByNum(rolNum.value().intValue()), doljnost.value(), fio.value(), s, main.value(), mobtel.value(), gortel.value()
									, new Task() {
										public void doTask() {
											loadData();
										}
									});
						}
					});

				}
			};
			secondLabel = "Удалить";
			secondTask = new Task() {
				@Override
				public void doTask() {
					promptDelete(kod, _fio);
				}
			};
		}


		Auxiliary.pick(this, "", new SubLayoutless(this)//
						.child(new Decor(this).labelText.is("контактное лицо").top().is(Auxiliary.tapSize * 0.2).left().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 5.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactToggle(this).yes.is(main).labelText.is("Основной").top().is(Auxiliary.tapSize * 0.5).left().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 1))//
						//.child(new RedactToggle(this).yes.is(main).labelText.is("Основной").width().is(Auxiliary.tapSize * 9).height().is(Auxiliary.tapSize * 1))//

						//.child(new Decor(this).labelText.is("контактное лицо").labelAlignRightTop().top().is(Auxiliary.tapSize * 1.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						//.child(new RedactText(this).text.is(kontakntnoeLico).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 0.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("роль").labelAlignRightTop().top().is(Auxiliary.tapSize * 2.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactSingleChoice(this).selection.is(rolNum)
								.item(rol00001).item(rol00002).item(rol00003).item(rol00004).item(rol00005).item(rol00006).item(rol00008)
								.left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 1.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("должность").labelAlignRightTop().top().is(Auxiliary.tapSize * 3.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(doljnost).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 2.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("ФИО").labelAlignRightTop().top().is(Auxiliary.tapSize * 4.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(fio).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 3.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("дата рождения").labelAlignRightTop().top().is(Auxiliary.tapSize * 5.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactDate(this).date.is(datarojd).format.is("dd.MM.yyyy").left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 4.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("мобильный тел.").labelAlignRightTop().top().is(Auxiliary.tapSize * 6.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(mobtel).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 5.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 1))//

						.child(new Decor(this).labelText.is("городской тел.").labelAlignRightTop().top().is(Auxiliary.tapSize * 7.0).width().is(Auxiliary.tapSize * 3.5).height().is(Auxiliary.tapSize * 1))//
						.child(new RedactText(this).text.is(gortel).left().is(Auxiliary.tapSize * 4.0).top().is(Auxiliary.tapSize * 6.5).width().is(Auxiliary.tapSize * 5).height().is(Auxiliary.tapSize * 1))//

						.width().is(Auxiliary.tapSize * 10)//
						.height().is(Auxiliary.tapSize * 8)//
				, firstLabel, firstTask, secondLabel, secondTask, null, null);
	}

	public String rol(String kod) {
		if (kod.equals("00001")) return rol00001;
		if (kod.equals("00002")) return rol00002;
		if (kod.equals("00003")) return rol00003;
		if (kod.equals("00004")) return rol00004;
		if (kod.equals("00005")) return rol00005;
		if (kod.equals("00006")) return rol00006;
		if (kod.equals("00008")) return rol00008;
		return "№" + kod;
	}

	public String rolByNum(int nn) {
		if (nn == 1) return "00002";
		if (nn == 2) return "00003";
		if (nn == 3) return "00004";
		if (nn == 4) return "00005";
		if (nn == 5) return "00006";
		if (nn == 6) return "00008";
		return "00001";
	}

	public int rolNumByKod(String kod) {
		if (kod.equals("00001")) return 0;
		if (kod.equals("00002")) return 1;
		if (kod.equals("00003")) return 2;
		if (kod.equals("00004")) return 3;
		if (kod.equals("00005")) return 4;
		if (kod.equals("00006")) return 5;
		if (kod.equals("00008")) return 6;
		return 0;
	}

	String timeDayName(int nn) {
		if (nn == 1) return "пн.";
		if (nn == 2) return "вт.";
		if (nn == 3) return "ср.";
		if (nn == 4) return "чт.";
		if (nn == 5) return "пт.";
		if (nn == 6) return "сб.";
		if (nn == 7) return "вс.";
		return "";
	}

	void formatVremyaRaboty(Vector<Bough> vrem) {
		for (int i = 0; i < fromTime.length; i++) {
			fromTime[i].value(0);
		}
		for (int i = 0; i < toTime.length; i++) {
			toTime[i].value(0);
		}
		SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");
		HHmm.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		String zpt = "";
		String timeString = "";
		for (int i = 0; i < vrem.size(); i++) {
			int dennedeli = (int) Numeric.string2double(vrem.get(i).child("ДеньНедели").value.property.value());
			System.out.println(dennedeli + ": " + timeDayName(dennedeli) + ": " + vrem.get(i).child("Начало").value.property.value());
			if ((!vrem.get(i).child("Начало").value.property.value().equals("00:00")) && vrem.get(i).child("Начало").value.property.value().length() > 1) {
				try {
					if (dennedeli < fromTime.length) {
						fromTime[dennedeli].value((double) HHmm.parse(vrem.get(i).child("Начало").value.property.value()).getTime());
					}
					if (dennedeli < toTime.length) {
						toTime[dennedeli].value((double) HHmm.parse(vrem.get(i).child("Конец").value.property.value()).getTime());
						timeString = timeString + zpt + timeDayName(dennedeli) + vrem.get(i).child("Начало").value.property.value() + " - " + vrem.get(i).child("Конец").value.property.value();
						zpt = ", ";
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		vremyaText.value(timeString);
		//String rabS = Auxiliary.tryReFormatDate(ActivityKlientPeople.this.vremyaRabotyS, "yyyyMMddHHmmss", "HH:mm");
		//String rabPo = Auxiliary.tryReFormatDate(ActivityKlientPeople.this.vremyaRabotyPo, "yyyyMMddHHmmss", "HH:mm");
		//vremyaText.value(rabS + " - " + rabPo);
		//vremyaText.value("e-mail: " + ActivityKlientPeople.this.pochta
		//+ ", время работы: " + ActivityKlientPeople.this.vremyaRaboty
		//+ " (" + rabS
		//+ " - " + rabPo
		//+ ")"
		//		+ ", время работы: " + rabS + " - " + rabPo
		//);
/*
<ВремяРаботы>
I/System.out: 			<ДеньНедели>4</ДеньНедели>
I/System.out: 			<Начало>11:02</Начало>
I/System.out: 			<Конец>16:59</Конец>
I/System.out: 		</ВремяРаботы>
*/
	}

	public void loadData() {
		ActivityKlientPeople.this.rows.removeAllElements();
		String kod = ApplicationHoreca.getInstance().getClientInfo().getKod();
		final String url = Settings.getInstance().getBaseURL() + Settings.selectedBase1C() + "/hs/ContactInfo/" + kod;
		//final String url = "https://testservice.swlife.ru/simutkin_hrc/hs/ContactInfo/" + kod;

		System.out.println(url);
		Expect expect = new Expect().status.is("Обновление списка").task.is(new Task() {
			@Override
			public void doTask() {
				try {
					byte[] bytes = Auxiliary.loadFileFromPrivateURL(url, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
					//.loadTextFromPrivatePOST(url, txt, 21000, "UTF-8",Cfg.hrcPersonalLogin,Cfg.hrcPersonalPassword);
					String txt = new String(bytes, "UTF-8");
					System.out.println(txt);
					Bough b = new Bough().parseJSON(txt);
					//System.out.println(b.dumpXML());
					ActivityKlientPeople.this.pochta = b.child("КонтактныеЛица").child("Почта").value.property.value();
					//ActivityKlientPeople.this.vremyaRaboty = b.child("КонтактныеЛица").child("ВремяРаботы").value.property.value();
					//ActivityKlientPeople.this.vremyaRabotyS = b.child("КонтактныеЛица").child("ВремяРаботыС").value.property.value();
					//ActivityKlientPeople.this.vremyaRabotyPo = b.child("КонтактныеЛица").child("ВремяРаботыПо").value.property.value();
					formatVremyaRaboty(b.child("КонтактныеЛица").children("ВремяРаботы"));

					ActivityKlientPeople.this.textPochta.value("e-mail: " + b.child("КонтактныеЛица").child("Почта").value.property.value());

					//klientRabotayetBezPechati.value(b.child("КонтактныеЛица").child("КлиентРаботаетБезПечати").value.property.value().trim().equals("true"));
					//textKlientRabotayetBezPechati.value(klientRabotayetBezPechati.value()?"да":"нет");
					//System.out.println(b.child("КонтактныеЛица").child("КлиентРаботаетБезПечати").value.property.value());

					ActivityKlientPeople.this.textVremya.value("Время работы: " + vremyaText.value());
					ActivityKlientPeople.this.textInn.value("ИНН: " + b.child("КонтактныеЛица").child("ИНН").value.property.value());
					ActivityKlientPeople.this.textKPP.value("КПП: " + b.child("КонтактныеЛица").child("КПП").value.property.value());
					ActivityKlientPeople.this.textBic.value("БИК: " + b.child("КонтактныеЛица").child("БИК").value.property.value());
					ActivityKlientPeople.this.textRC.value("Расч.счет: " + b.child("КонтактныеЛица").child("РС").value.property.value());
					ActivityKlientPeople.this.textAdrFact.value("Адрес факт.: " + b.child("КонтактныеЛица").child("АдресФактический").value.property.value());
					ActivityKlientPeople.this.textAdrDost.value("Адрес доставки: " + b.child("КонтактныеЛица").child("АдресДоставки").value.property.value());
					oldAdresDostavki = b.child("КонтактныеЛица").child("АдресДоставки").value.property.value();
					ActivityKlientPeople.this.textAdrUr.value("Адрес юр.: " + b.child("КонтактныеЛица").child("АдресЮридический").value.property.value());
					ActivityKlientPeople.this.textOsnovKlient.value("Осн. клиент ТТ: " + b.child("КонтактныеЛица").child("ОсновнойКлиентТТ").value.property.value());
					ActivityKlientPeople.this.osobennosti.value(b.child("КонтактныеЛица").child("ОсобенностиКлиента").value.property.value());


					ActivityKlientPeople.this.KommentariiPropusk.value(b.child("КонтактныеЛица").child("КомментарийПропуск").value.property.value());
					ActivityKlientPeople.this.osobennostiRejimaRaboty.value(b.child("КонтактныеЛица").child("ОсобенностиРежимаРаботы").value.property.value());


					ActivityKlientPeople.this.valNDS.value(b.child("КонтактныеЛица").child("ПлательщикНДС").value.property.value());
					if (valNDS.value().equals("-1"))
						ActivityKlientPeople.this.textNDS.value("Плательщик НДС: -");
					if (valNDS.value().equals("0"))
						ActivityKlientPeople.this.textNDS.value("Плательщик НДС: нет");
					if (valNDS.value().equals("1"))
						ActivityKlientPeople.this.textNDS.value("Плательщик НДС: да");


					Vector<Bough> people = b.child("КонтактныеЛица").children("КонтактныеЛица");
					for (int i = 0; i < people.size(); i++) {
						ActivityKlientPeople.this.rows.add(people.get(i));
						//System.out.println(people.get(i).dumpXML());
					}


				} catch (Throwable t) {
					t.printStackTrace();
				}

			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				flipItemsGrid();
				itemsGrid.refresh();

			}
		});
		expect.start(this);
	}


	public void flipItemsGrid() {
		itemsGrid.clearColumns();
		if (rows != null) {

			for (int i = 0; i < rows.size(); i++) {
				Bough row = rows.get(i);
				//final String art = row.child("Артикул").value.property.value();
				// final String name = row.child("name").value.property.value();
				final String kod = row.child("КодКонтактногоЛица").value.property.value();
				//final String name = row.child("КонтактноеЛицо").value.property.value();
				final boolean main = row.child("Основное").value.property.value().equals("true");
				final int rolNum = rolNumByKod(row.child("КодРоль").value.property.value());
				final String doljnost = row.child("Должность").value.property.value();
				final String fio = row.child("ФИО").value.property.value();
				long dms = 0;
				try {
					dms = Auxiliary.short1cDate.parse(row.child("ДатаРождения").value.property.value()).getTime();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				final long datarojd = dms;
				final String mobtel = row.child("МобильныйТелефон").value.property.value();
				final String gortel = row.child("ГородскойТелефон").value.property.value();

				Task tap = new Task() {
					@Override
					public void doTask() {
						//promptDelete(kod, name);
						//promptInfo(art);
						promptEditOrDelete(kod, main, rolNum, doljnost, fio, datarojd, mobtel, gortel);
					}
				};//mOrder
				String info = row.child("КонтактноеЛицо").value.property.value();
				if (mobtel.length() > 0) {
					info = info + ", моб.тел: " + mobtel;
				}
				if (gortel.length() > 0) {
					info = info + ", гор.тел: " + gortel;
				}
				String descr = "код: " + kod;
				if (kod.startsWith("/"))
					descr = "";
				if (row.child("Должность").value.property.value().length() > 0) {
					descr = descr + ", должность: " + doljnost;
				}
				if (row.child("КодРоль").value.property.value().length() > 0) {
					descr = descr + ", роль: " + rol(row.child("КодРоль").value.property.value());
				}
				if (row.child("ФИО").value.property.value().length() > 0) {
					descr = descr + ", ФИО: " + fio;
				}
				if (row.child("ДатаРождения").value.property.value().length() > 0) {
					descr = descr + ", д.р.: " + Auxiliary.tryReFormatDate(row.child("ДатаРождения").value.property.value(), "yyyyMMdd", "dd.MM.YYYY");
				}
				if (row.child("Основное").value.property.value().length() > 0) {
					descr = descr + ", основной: " + (main ? "да" : "нет");
				}
				columnItemNaimenovanie.cell(info, tap, descr);

			}
		}
	}

	void promptEditOrDelete(final String kod, boolean _main, int _rolNum, String _doljnost, String _fio, long _datarojd, String _mobtel, String _gortel) {
		promptDobavit(kod, _main, _rolNum, _doljnost, _fio, _datarojd, _mobtel, _gortel);
	}

	void promptDelete(final String kod, String name) {
		Auxiliary.pickConfirm(ActivityKlientPeople.this, "Удалить контакт №" + kod + ", " + name, "Удалить", new Task() {
			@Override
			public void doTask() {
				System.out.println("promptDelete " + kod);
				sendDelete(kod, new Task() {
					public void doTask() {
						loadData();
					}
				});
			}
		});
	}

	void sendDelete(String kod, Task doAfterDone) {

		String json = "[";
		json = json + "\n {";
		json = json + "\n \"Почта\":\"" + this.pochta + "\"";
		//json = json + "\n ,\"ВремяРаботы\":\"" + this.vremyaRaboty + "\"";
		//json = json + "\n ,\"ВремяРаботыС\":\"" + this.vremyaRabotyS + "\"";
		//json = json + "\n ,\"ВремяРаботыПо\":\"" + this.vremyaRabotyPo + "\"";
		//json = json + "\n ,\"ПлательщикНДС\":\"" + valNDS.value() + "\"";
		//String zpt = "";
		SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");
		HHmm.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		json = json + "\n ,\"ВремяРаботы\":[";
		json = json + "{\"ДеньНедели\": 0, \"Начало\": \"" + (fromTime[0].value() > 0 ? HHmm.format(new Date(fromTime[0].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[0].value() > 0 ? HHmm.format(new Date(toTime[0].value().longValue())) : "") + "\" }";
		json = json + ",{\"ДеньНедели\": 1, \"Начало\": \"" + (fromTime[1].value() > 0 ? HHmm.format(new Date(fromTime[1].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[1].value() > 0 ? HHmm.format(new Date(toTime[1].value().longValue())) : "") + "\" }";
		json = json + ",{\"ДеньНедели\": 2, \"Начало\": \"" + (fromTime[2].value() > 0 ? HHmm.format(new Date(fromTime[2].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[2].value() > 0 ? HHmm.format(new Date(toTime[2].value().longValue())) : "") + "\" }";
		json = json + ",{\"ДеньНедели\": 3, \"Начало\": \"" + (fromTime[3].value() > 0 ? HHmm.format(new Date(fromTime[3].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[3].value() > 0 ? HHmm.format(new Date(toTime[3].value().longValue())) : "") + "\" }";
		json = json + ",{\"ДеньНедели\": 4, \"Начало\": \"" + (fromTime[4].value() > 0 ? HHmm.format(new Date(fromTime[4].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[4].value() > 0 ? HHmm.format(new Date(toTime[4].value().longValue())) : "") + "\" }";
		json = json + ",{\"ДеньНедели\": 5, \"Начало\": \"" + (fromTime[5].value() > 0 ? HHmm.format(new Date(fromTime[5].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[5].value() > 0 ? HHmm.format(new Date(toTime[5].value().longValue())) : "") + "\" }";
		json = json + ",{\"ДеньНедели\": 6, \"Начало\": \"" + (fromTime[6].value() > 0 ? HHmm.format(new Date(fromTime[6].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[6].value() > 0 ? HHmm.format(new Date(toTime[6].value().longValue())) : "") + "\" }";
		json = json + ",{\"ДеньНедели\": 7, \"Начало\": \"" + (fromTime[7].value() > 0 ? HHmm.format(new Date(fromTime[7].value().longValue())) : "")
				+ "\", \"Конец\": \"" + (toTime[7].value() > 0 ? HHmm.format(new Date(toTime[7].value().longValue())) : "") + "\" }";
		/*if (this.toTime[0].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 0, \"Начало\": \"" + HHmm.format(new Date(fromTime[0].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[0].value().longValue())) + "\" }";
			zpt = ", ";
		}
		if (this.toTime[1].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 1, \"Начало\": \"" + HHmm.format(new Date(fromTime[1].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[1].value().longValue())) + "\" }";
			zpt = ", ";
		}
		if (this.toTime[2].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 2, \"Начало\": \"" + HHmm.format(new Date(fromTime[2].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[2].value().longValue())) + "\" }";
			zpt = ", ";
		}
		if (this.toTime[3].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 3, \"Начало\": \"" + HHmm.format(new Date(fromTime[3].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[3].value().longValue())) + "\" }";
			zpt = ", ";
		}
		if (this.toTime[4].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 4, \"Начало\": \"" + HHmm.format(new Date(fromTime[4].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[4].value().longValue())) + "\" }";
			zpt = ", ";
		}
		if (this.toTime[5].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 5, \"Начало\": \"" + HHmm.format(new Date(fromTime[5].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[5].value().longValue())) + "\" }";
			zpt = ", ";
		}
		if (this.toTime[6].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 6, \"Начало\": \"" + HHmm.format(new Date(fromTime[6].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[6].value().longValue())) + "\" }";
			zpt = ", ";
		}
		if (this.toTime[7].value() > 0) {
			json = json+zpt + "{\"ДеньНедели\": 7, \"Начало\": \"" + HHmm.format(new Date(fromTime[7].value().longValue()))
					+ "\", \"Конец\": \"" + HHmm.format(new Date(toTime[7].value().longValue())) + "\" }";
			zpt = ", ";
		}*/
		json = json + "\n ]";
		/*
		json = json + "\n ,\"ВремяРаботы\":["
				+ "{\"ДеньНедели\": 0, \"Начало\": \"11:20\", \"Конец\": \"19:35\" }"
				+ ",{\"ДеньНедели\": 1, \"Начало\": \"9:00\", \"Конец\": \"17:45\" }"
				+ ",{\"ДеньНедели\": 5, \"Начало\": \"10:00\", \"Конец\": \"12:15\" }"
				+ "]";
		*/
		json = json + "\n ,\"ОсобенностиКлиента\":\"" + osobennosti.value().replace('"', '`') + "\"";
		json = json + "\n ,\"КомментарийПропуск\":\"" + this.KommentariiPropusk.value().replace('"', '`') + "\"";
		//json = json + "\n ,\"ОсобенностиРежимаРаботы\":\"" + this.osobennostiRejimaRaboty.value().replace('"', '`') + "\"";
		json = json + "\n ,\"ОсобенностиРежимаРаботы\":\"" + this.osobennostiRejimaRaboty.value().replace('"', '`') + "\"";
		json = json + "\n ,\"КомментарийКРежимуРаботы\":\"" + this.osobennostiRejimaRaboty.value().replace('"', '`') + "\"";


		//json = json + "\n ,\"КлиентРаботаетБезПечати\":" + (this.klientRabotayetBezPechati.value()?"true":"false") + "";
		json = json + "\n ,\"КлиентРаботаетБезПечати\":false";
		json = json + "\n ,\"Удаленные\":[";
		if (kod.length() > 1) {
			json = json + "\n   {\"КодКонтактногоЛица\": \"" + kod + "\"}";
		}
		json = json + "\n  ]";
		json = json + "\n ,\"КонтактныеЛица\":[]";
		json = json + "\n }";
		json = json + "\n]";

		System.out.println(json);
		sendData(json, doAfterDone);
	}

	void sendAdd(String kontaktLico, String kodRol, String doljnost, String fio, String dataRojd, boolean osnov, String mobTel, String gorTel, Task doAfterDone) {
		String main = "false";
		if (osnov) {
			main = "true";
		}

		String json = "[";
		json = json + "\n {";
		json = json + "\n \"Почта\":\"" + this.pochta + "\"";
		//json = json + "\n ,\"ВремяРаботы\":\"" + this.vremyaRaboty + "\"";
		//json = json + "\n ,\"ВремяРаботыС\":\"" + this.vremyaRabotyS + "\"";
		//json = json + "\n ,\"ВремяРаботыПо\":\"" + this.vremyaRabotyPo + "\"";
		json = json + "\n ,\"ПлательщикНДС\":\"" + valNDS.value() + "\"";
		json = json + "\n ,\"Удаленные\":[]";
		json = json + "\n ,\"КонтактныеЛица\":[";
		json = json + "\n  {\"КодКонтактногоЛица\": \"\""
				+ "\n     ,\"КонтактноеЛицо\": \"" + kontaktLico + "\""
				+ "\n     ,\"КодРоль\": \"" + kodRol + "\""
				+ "\n     ,\"Должность\": \"" + doljnost + "\""
				+ "\n     ,\"ФИО\": \"" + fio + "\""
				+ "\n     ,\"ДатаРождения\": \"" + dataRojd + "\""
				+ "\n     ,\"Основное\": " + main + ""
				+ "\n     ,\"МобильныйТелефон\": \"" + mobTel + "\""
				+ "\n     ,\"ГородскойТелефон\": \"" + gorTel + "\""
				+ "\n   }";
		json = json + "\n  ]";
		json = json + "\n }";
		json = json + "\n]";

        /*String dlmtr = "";
        for (int i = 0; i < rows.size(); i++) {
            Bough row = rows.get(i);
            String num=row.child("КодКонтактногоЛица").value.property.value();
            if(num.startsWith("/")){
                num="";
            }
            String main="false";
            if(row.child("Основное").value.property.value().equals("true")){
                main="true";
            }
            json = json + dlmtr + "{\"КодКонтактногоЛица\": \"" +num + "\""
                    + ",\"КонтактноеЛицо\": \"" + row.child("КонтактноеЛицо").value.property.value() + "\""
                    + ",\"КодРоль\": \"" + row.child("КодРоль").value.property.value() + "\""
                    + ",\"Должность\": \"" + row.child("Должность").value.property.value() + "\""
                    + ",\"ФИО\": \"" + row.child("ФИО").value.property.value() + "\""
                    + ",\"ДатаРождения\": \"" + row.child("ДатаРождения").value.property.value() + "\""
                    + ",\"Основное\": " + main + ""
                    + ",\"МобильныйТелефон\": \"" + row.child("МобильныйТелефон").value.property.value() + "\""
                    + ",\"ГородскойТелефон\": \"" + row.child("ГородскойТелефон").value.property.value() + "\""
                    + "}";
            dlmtr = ",";
        }
        json = json + "]}]";
        */
		System.out.println(json);
		sendData(json, doAfterDone);
	}

	void sendData(final String json, final Task doAfterDone) {
		final String url =
				Settings.getInstance().getBaseURL() + Settings.selectedBase1C()
						//"http://89.109.7.162/shatov"
						//"https://testservice.swlife.ru/simutkin_hrc/"
						+ "/hs/ContactInfo/Peredat/"
						+ ApplicationHoreca.getInstance().getClientInfo().getKod().trim();
		final Note msg = new Note();
		Expect expect = new Expect().status.is("Отправка").task.is(new Task() {
			@Override
			public void doTask() {
				System.out.println(url);
				System.out.println(json);
				Bough result = Auxiliary.loadTextFromPrivatePOST(url, json, 21000, "UTF-8", Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());
				System.out.println(result.dumpXML());
				msg.value(result.child("message").value.property.value() + " " + result.child("raw").value.property.value());
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				Auxiliary.inform(msg.value(), ActivityKlientPeople.this);
				//loadData();
				if (doAfterDone != null) doAfterDone.doTask();
			}
		});
		expect.start(this);
	}
}
