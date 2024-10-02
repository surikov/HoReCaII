package sweetlife.android10.ui;

import java.util.ArrayList;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.data.common.ClientInfo;
import sweetlife.android10.database.Request_ClientsByContractsGroup;
import sweetlife.android10.database.Request_ClientsList;
import sweetlife.android10.database.Request_ContactGroups;
import sweetlife.android10.database.Request_Contracts;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.reports.ClientsAndContractsListAdapter;
import sweetlife.android10.reports.ClientsListAdapter;
import sweetlife.android10.reports.ContactGroupsListAdapter;
import sweetlife.android10.reports.IReportConsts;
import sweetlife.android10.reports.ReportBalanseInfo;
import sweetlife.android10.reports.ReportBalanseXMLSerializer;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.UIHelper;
import sweetlife.android10.utils.DialogTask.IDialogTaskAction;

import sweetlife.android10.*;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioButton;

public class Activity_ReportBalance extends Activity_ReportBase implements IReportConsts, IDialogTaskAction {

	private Request_ClientsList mClientsRequestHelper;

	private AlertDialog mClientsDialog;
	private AlertDialog mContractGroupsDialog;

	private ClientsAndContractsListAdapter mClientsAndContractsListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.act_report_balance);

		super.onCreate(savedInstanceState);

		setTitle(R.string.partner_balance);

		mClientsRequestHelper = new Request_ClientsList();
	}

	private OnClickListener mChooseClientClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			ShowClientsListDialog();
		}
	};

	private OnClickListener mChooseContactsGroupsClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			ShowContractGroupsDialog();
		}
	};

	protected void InitializeControls() {

		super.InitializeControls();

		Button btnClients = (Button) findViewById(R.id.btn_by_clients);
		btnClients.setOnClickListener(mChooseClientClick);

		Button btnContractsGroups = (Button) findViewById(R.id.btn_by_contracts_groups);
		btnContractsGroups.setOnClickListener(mChooseContactsGroupsClick);

		InitializeListView();
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, IDD_DELETE, 1, getString(R.string.delete));
	}

	public boolean onContextItemSelected(MenuItem menuItem) {

		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuItem.getMenuInfo();

		int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);

		mClientsAndContractsListAdapter.removeItem(groupPos);

		return true;
	}

	private void InitializeListView() {

		ExpandableListView listView = (ExpandableListView) findViewById(R.id.list_clients);

		registerForContextMenu(listView);

		mClientsAndContractsListAdapter = new ClientsAndContractsListAdapter(this);

		listView.setAdapter(mClientsAndContractsListAdapter);
	}

	private void AddClientToList(String clientID) {

		Request_Contracts request = new Request_Contracts(mDB, clientID);

		ClientInfo info = new ClientInfo(mDB, clientID, request.getContractsList());

		mClientsAndContractsListAdapter.addItem(info);
	}

	private void ShowClientsListDialog() {

		ListView clientsList = (ListView) getLayoutInflater().inflate(R.layout.dialog_list,
				(ListView) findViewById(R.id.list));

		clientsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
									int position, long id) {

				ListView clientsList = (ListView) adapterView;
				Cursor cursor = ((ClientsListAdapter) clientsList.getAdapter()).getCursor();

				AddClientToList(Request_ClientsList.getClientID(cursor));

				mClientsDialog.dismiss();
			}
		});

		ClientsListAdapter clientsListAdapter = new ClientsListAdapter(this,
				mClientsRequestHelper.Request(mDB, 0, null));

		clientsList.setAdapter(clientsListAdapter);
		LogHelper.debug(this.getClass().getCanonicalName() + ".ShowClientsListDialog: " + getString(R.string.choose_client));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		dialogBuilder.setView(clientsList);

		dialogBuilder.setTitle(getString(R.string.choose_client));

		mClientsDialog = dialogBuilder.create();

		mClientsDialog.show();
	}

	private void ShowContractGroupsDialog() {

		ListView contractGroupsList = (ListView) getLayoutInflater().inflate(R.layout.dialog_list,
				(ListView) findViewById(R.id.list));

		contractGroupsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
									int position, long id) {

				ListView contractGroupsList = (ListView) adapterView;
				Cursor cursor = ((ContactGroupsListAdapter) contractGroupsList.getAdapter()).getCursor();

				Request_ClientsByContractsGroup request = new Request_ClientsByContractsGroup(mDB,
						Request_ContactGroups.getContactsGroupID(cursor));

				ArrayList<String> clients = request.getClientsList();

				for (int i = 0; i < clients.size(); i++) {

					AddClientToList(clients.get(i));
				}

				mContractGroupsDialog.dismiss();
			}
		});

		ContactGroupsListAdapter clientsListAdapter = new ContactGroupsListAdapter(this,
				Request_ContactGroups.Request(mDB));

		contractGroupsList.setAdapter(clientsListAdapter);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		dialogBuilder.setView(contractGroupsList);

		dialogBuilder.setTitle(getString(R.string.choose_contracts_group));

		mContractGroupsDialog = dialogBuilder.create();

		mContractGroupsDialog.show();
	}

	protected String reportRequest() throws Exception {


		ReportBalanseInfo reportInfo = new ReportBalanseInfo(REPORT_TYPE_BALANSE,
				DateTimeHelper.SQLDateString(mFromPeriod.getTime()) + "T00:00:00",
				DateTimeHelper.SQLDateString(mToPeriod.getTime()) + "T23:59:59",
				ApplicationHoreca.getInstance().getCurrentAgent().getAgentKodTrim(),
				getClientCodes(),
				getReportType(),
				getOnlyGroupContracts());

		return reportRequest(reportInfo, new ReportBalanseXMLSerializer(reportInfo));
	}

	private ArrayList<String> getClientCodes() {

		int clientsCount = mClientsAndContractsListAdapter.getGroupCount();

		ArrayList<String> clientsList = new ArrayList<String>(clientsCount);

		ClientInfo info;

		for (int i = 0; i < clientsCount; i++) {

			info = ((ClientInfo) mClientsAndContractsListAdapter.getGroup(i));

			clientsList.add(info.getKod().trim());
		}

		return clientsList;
	}

	private int getReportType() {

		if (((RadioButton) findViewById(R.id.radio_expanded)).isChecked()) {

			return TYPE_EXPANDED;
		}

		if (((RadioButton) findViewById(R.id.radio_combined)).isChecked()) {

			return TYPE_COMBINED;
		}

		if (((RadioButton) findViewById(R.id.radio_collapsed)).isChecked()) {

			return TYPE_COLLAPSED;
		}

		if (((RadioButton) findViewById(R.id.radio_expanded_by_contracts)).isChecked()) {

			return TYPE_EXPANDED_BY_CONTACTS;
		}

		if (((RadioButton) findViewById(R.id.radio_collapsed_by_contracts)).isChecked()) {

			return TYPE_COLLAPSED_BY_CONTACTS;
		}

		return TYPE_EXPANDED;
	}

	private boolean getOnlyGroupContracts() {

		return ((CheckBox) findViewById(R.id.check_only_group_contracts)).isChecked() ? true : false;
	}

	protected boolean validateData() {

		if (mClientsAndContractsListAdapter.getGroupCount() == 0) {

			UIHelper.MsgBox(getString(R.string.error),
					getString(R.string.error_empty_client_list),
					this, null);

			return false;
		}

		return super.validateData();
	}
}
