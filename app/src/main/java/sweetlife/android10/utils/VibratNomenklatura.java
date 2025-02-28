package sweetlife.android10.utils;

import android.app.*;
import android.content.*;

import java.util.*;

import reactive.ui.*;
import sweetlife.android10.*;
import sweetlife.android10.data.common.*;
import sweetlife.android10.database.nomenclature.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.*;

public class VibratNomenklatura{
	Context curContext;
	Task onSave;
	DataGrid grid;
	DataGrid selegrid;
	ColumnText lines = new ColumnText();
	ColumnText selelines = new ColumnText();
	Vector<String> artikuly = new Vector<String>();
	Vector<String> texty = new Vector<String>();
	Note filter= new Note();
	/*
	CannyTask vibratNomenklaturaSearch;

	public VibratNomenklatura(){
		//this.context = context;

		vibratNomenklaturaSearch = new CannyTask(){
			@Override
			public void doTask(){
				filterList();
			}
		};
		vibratNomenklaturaSearch.laziness.is(500);
		this.filter = new Note().afterChange(vibratNomenklaturaSearch, true);
	}
*/

	public void showArtDialog(Context context, Vector<String> arts, Task onSave){
		artikuly = arts;
		texty.removeAllElements();
		for(int ii = 0; ii < arts.size(); ii++){
			String sql="select nn.Naimenovanie as name from nomenklatura nn where nn.artikul='"+arts.get(ii).trim()+"';";
			Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			texty.add(data.child("row").child("name").value.property.value()+", арт. " + arts.get(ii));
			//System.out.println("add " + arts.get(ii));
		}
		/*
		artikuly.removeAllElements();
		String[] parts = arts.split(",");
		texty.removeAllElements();
		for(int ii = 0; ii < parts.length; ii++){
			artikuly.add(parts[ii]);
			texty.add("арт. " + parts[ii]);
			System.out.println("add " + parts[ii]);
		}*/
		curContext = context;
		this.onSave = onSave;
		grid = new DataGrid(context);
		selegrid = new DataGrid(context);
		String title = "" + ApplicationHoreca.getInstance().getClientInfo().getKod()
				+ ": " + ApplicationHoreca.getInstance().getClientInfo().getName()
				+ " (" + ApplicationHoreca.getInstance().getCurrentAgent().getPodrazdelenieName()
				+ ", " + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod()
				+ ")";
		AlertDialog alertDialog = Auxiliary.pick(context, title, new SubLayoutless(context)//
						.child(new Knob(context).labelText.is("поиск").afterTap.is(new Task(){
									@Override
									public void doTask(){
										filterList();
									}
								})
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 2)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(new RedactText(context).text.is(filter)
								.left().is(Auxiliary.tapSize * 2.5)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 7)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(grid.noHead.is(true).columns(
										new Column[]{
												lines.title.is("?").width.is(Auxiliary.tapSize * 9)
										})
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 1.0)
								.width().is(Auxiliary.tapSize * 9)
								.height().is(Auxiliary.tapSize * 8))
						.child(selegrid.noHead.is(true).columns(
										new Column[]{
												selelines.title.is("Выбрано").width.is(Auxiliary.tapSize * 9.5)
										})
								.left().is(Auxiliary.tapSize * 10)
								.top().is(Auxiliary.tapSize * 0.5)
								.width().is(Auxiliary.tapSize * 9.5)
								.height().is(Auxiliary.tapSize * 8.5))
						.width().is(Auxiliary.tapSize * 20)//
						.height().is(Auxiliary.tapSize * 11)//
				, "Сохранить", onSave
				, null, null
				, null, null);

		refreshSelectedItems();
	}

	public String dataRequest(String searchWord){
		Date denOtgruzki = NomenclatureBasedDocument.nextWorkingDate(Calendar.getInstance());
		String kontragentXhex = ApplicationHoreca.getInstance().getClientInfo().getID();
		String torgoviyXhex = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
		int itemsMaxCount = 99;
		String sql = Request_NomenclatureBase.composeSQLall_Old(
				DateTimeHelper.SQLDateString(denOtgruzki)
				, kontragentXhex
				, torgoviyXhex
				, null//DateTimeHelper.SQLDateString(fr)//
				, null//DateTimeHelper.SQLDateString(to)//
				, " n.artikul like '%" + searchWord.trim()
						+ "%' or n.UpperName like '%" + searchWord.toUpperCase().trim()
						+ "%' or n.tegi like '%" + searchWord.toUpperCase().trim() + "%' "//
				, ISearchBy.SEARCH_CUSTOM
				, false//
				, false//history
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()//
				, itemsMaxCount//gridPageSize * 3//
				//, gridHistory.dataOffset.property.value().intValue()//
				, 0//
				, false//
				, false, null, null, false, false, null, null, null
				, false//,filterBySTM.value()
				, false
				, false
				, false
				, false
				, false
		);
		return sql;
	}

	void deleteFromSelected(String delart){
		for(int kk = 0; kk < texty.size(); kk++){
			if(artikuly.get(kk).equals(delart)){
				artikuly.remove(kk);
				texty.remove(kk);
				Auxiliary.warn("Артикул " + delart + " удалён из выбранного", curContext);
				refreshSelectedItems();
				return;
			}
		}
	}

	void refreshSelectedItems(){
		selegrid.clearColumns();
		for(int i = 0; i < texty.size(); i++){
			final String delart = artikuly.get(i);
			Task tap = new Task(){
				@Override
				public void doTask(){
					deleteFromSelected(delart);
				}
			};
			selelines.cell(texty.get(i), tap);
		}
		selegrid.refresh();
	}

	void addToSelected(String art, String seltext){
		System.out.println("select " + art);
		for(int i = 0; i < texty.size(); i++){
			if(artikuly.get(i).equals(art)){
				return;
			}
		}
		artikuly.add(art);
		texty.add(seltext);
		Auxiliary.warn("Артикул " + art + " добавлен в выбранное", curContext);
		refreshSelectedItems();
	}

	void filterList(){
		System.out.println("reset vibratNomenklatura " + (new Date().toString()));
		String sql = dataRequest(filter.value());
		Bough data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		System.out.println(data.dumpXML());
		grid.clearColumns();

		for(int i = 0; i < data.children.size(); i++){
			Bough row = data.children.get(i);
			String _idrref = row.child("_IDRRef").value.property.value();
			String descript = row.child("Naimenovanie").value.property.value()
					+ ", прайс " + row.child("Cena").value.property.value() + "р";
			String VidSkidki = row.child("VidSkidki").value.property.value();
			if(VidSkidki.length() > 0){
				descript = descript + ", " + VidSkidki + " " + row.child("Skidka").value.property.value() + "р";
			}
			descript = descript + ", арт." + row.child("Artikul").value.property.value();
			final String art = row.child("Artikul").value.property.value();
			final String txt = descript;
			final String seltext = row.child("Naimenovanie").value.property.value() + ", арт." + row.child("Artikul").value.property.value();
			Task tap = new Task(){
				@Override
				public void doTask(){
					addToSelected(art, seltext);
				}
			};
			lines.cell(descript, tap);
		}
		grid.refresh();

	}


}
