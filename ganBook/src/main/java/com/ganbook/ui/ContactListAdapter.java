package com.ganbook.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ganbook.activities.AddKidActivity;
import com.ganbook.activities.ChooseClassActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.datamodel.ClassDetails_2;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.fragments.MainScreenFragment;
import com.ganbook.fragments.SingleContactProfileFragement;
import com.ganbook.interfaces.ContactsFragmentInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.ListUpdaresInterface;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.RefreshDrawerEvent;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.ActiveUtils;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.Const;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.StrUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactListAdapter extends BaseAdapter implements Filterable, ListUpdaresInterface {

	private static final String TAG = ContactListAdapter.class.getName();
	private ContactsFragmentInterface contactsFragmentInterface;
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<GetParentAnswer> arrayList;
	private ArrayList<GetParentAnswer> orig_arrayList;
	private ValueFilter valueFilter;
	private GetUserKids_Response[] updateKids;
	SweetAlertDialog progress;

	@Named("POST")
	@Inject
	GanbookApiInterface ganbookApiInterfacePOST;

	public ContactListAdapter(Context context) {

		this.context = context;
		this.inflater = LayoutInflater.from(context);
		arrayList = new ArrayList<>();
	}

	public ContactListAdapter(Context context, ContactsFragmentInterface contactsFragmentInterface) {

		this.context = context;
		this.inflater = LayoutInflater.from(context);
		arrayList = new ArrayList<>();
		this.contactsFragmentInterface = contactsFragmentInterface;
		progress = AlertUtils.createProgressDialog(context,
				context.getString(R.string.operation_proceeding));

		((MyApp) ((Activity) context).getApplication()).getGanbookApiComponent().inject(this);
	}

	@Override
	public Filter getFilter() {

		if(valueFilter == null) {

			valueFilter = new ValueFilter();
		}

		return valueFilter;
	}

	public void addAll(ArrayList<GetParentAnswer> parentsArr) {

		if (arrayList != null) {
			this.arrayList.addAll(parentsArr);
			this.orig_arrayList = (ArrayList<GetParentAnswer>) parentsArr.clone();
		}
		notifyDataSetChanged();
		getFilter();
	}

	public void clear() {

		if (arrayList!=null)
		arrayList.clear();
		if (orig_arrayList!=null)
		orig_arrayList.clear();
		notifyDataSetChanged();
	}

	public class ViewHolder {
		TextView con;
		CircleImageView img;
		Button btn;
		Button btn_ed;
		Button more_btn;
		TextView pta;
		Switch kid_arrival;
		ImageButton kidDetails;
	}
	
	@Override
	public int getCount() {
		return arrayList != null ? arrayList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.contactlist_inflator, new LinearLayout(context));
			holder.con = (TextView) view.findViewById(R.id.cont);
		    holder.img = (CircleImageView) view.findViewById(R.id.profile_image);
		    holder.btn = (Button) view.findViewById(R.id.confirm_btn);
		    holder.btn_ed = (Button) view.findViewById(R.id.confirmed_btn);
			holder.more_btn = (Button) view.findViewById(R.id.more_btn);
			holder.pta = (TextView) view.findViewById(R.id.pta_text);
			holder.kid_arrival = view.findViewById(R.id.kid_arrival_switch);
			holder.kidDetails  = view.findViewById(R.id.edit_kid_details);

			view.setTag(holder);
		} 
		else {
			holder = (ViewHolder) view.getTag();
		}
		 
		final GetParentAnswer current = arrayList.get(position);

		Log.d(TAG, "getView: current = " + current);
		
//		boolean isMother = current.isMother();
		int defImageResId = current.getDeafultKidImg();
		
//		holder.img.setImageResource(defImageResId);
		holder.con.setText(current.kid_name);		 
		view.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {

			SingleContactProfileFragement singleContactProfileFragement =
					SingleContactProfileFragement.newInstance(current);

			singleContactProfileFragement.setListUpdaresInterface(ContactListAdapter.this);

			FragmentUtils.openFragment(singleContactProfileFragement, R.id.content_frame,
					SingleContactProfileFragement.TAG, context, true);
			}
		});
		
		String kid_pic = current.kid_pic;
		if (StrUtils.notEmpty(kid_pic)) {	
			getKidPicture(current, kid_pic, holder.img, defImageResId);
		}
		else
		{
			holder.img.setImageResource(defImageResId);
		}

		if(current.vaad_type != null && !"0".equals(current.vaad_type))
		{
			if(current.type.equals("5")) {
				holder.pta.setVisibility(View.VISIBLE);
				holder.pta.setText(R.string.staff_string);
				holder.kid_arrival.setVisibility(View.GONE);
				//holder.kidDetails.setVisibility(View.GONE);

			} else if(current.type.equals("6")){
				holder.pta.setVisibility(View.VISIBLE);
				holder.pta.setText("Student");
				holder.kid_arrival.setVisibility(View.GONE);
				//holder.kidDetails.setVisibility(View.GONE);
			} else {
				if(User.getEmail().equals(current.parent_mail)) {
					//holder.kidDetails.setVisibility(View.VISIBLE);
					editKidClick(holder, current);
				} else {
					//holder.kidDetails.setVisibility(View.GONE);

				}
				holder.kid_arrival.setVisibility(View.GONE);
				holder.pta.setText(R.string.PTA);
				holder.pta.setVisibility(View.VISIBLE);
			}

		}
		else
		{
			if(current.type.equals("5")) {
				holder.kid_arrival.setVisibility(View.GONE);
				holder.pta.setVisibility(View.VISIBLE);
				holder.kid_arrival.setVisibility(View.GONE);
				holder.pta.setText(R.string.staff_string);
				//holder.kidDetails.setVisibility(View.GONE);

			} else if (current.type.equals("6")) {
				holder.kid_arrival.setVisibility(View.GONE);
				holder.pta.setVisibility(View.VISIBLE);
				holder.kid_arrival.setVisibility(View.GONE);
				holder.pta.setText(R.string.student);
				//holder.kidDetails.setVisibility(View.GONE);
			} else {
				holder.pta.setVisibility(View.GONE);
				if(User.isTeacher()) {
					holder.kid_arrival.setVisibility(View.VISIBLE);

					if(current.attendance.equals("1")) {
						holder.kid_arrival.setChecked(true);
					} else {
						holder.kid_arrival.setChecked(false);
					}
					holder.kid_arrival.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if(isChecked){
								updateKidAttendance(current.kid_id, User.current.getCurrentGanId(), "1");
							} else {
								updateKidAttendance(current.kid_id, User.current.getCurrentGanId(), "0");
							}
						}
					});

				} else if(User.isStaff()){
					//holder.kidDetails.setVisibility(View.GONE);
				} else {
					if(User.getEmail().equals(current.parent_mail)) {
						//holder.kidDetails.setVisibility(View.VISIBLE);

						editKidClick(holder, current);
					} else {
						//holder.kidDetails.setVisibility(View.GONE);
					}
				}

			}

		}


		
		if(User.isTeacher())
		{
			if(!ActiveUtils.isActive(current.kid_active))
			{
				holder.btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Call<SuccessAnswer> kidActive = ganbookApiInterfacePOST.activeKid(current.kid_id);

						progress.show();

						kidActive.enqueue(new Callback<SuccessAnswer>() {
							@Override
							public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

								progress.hide();

								SuccessAnswer successAnswer = response.body();

								if (successAnswer != null) {

									Log.d(TAG, "onResponse: success = " + successAnswer);

									if (successAnswer.isSuccess()) {

										holder.btn.setVisibility(View.GONE);
										holder.more_btn.setVisibility(View.GONE);

										holder.btn_ed.setVisibility(View.VISIBLE);

										for(GetParentAnswer parentResponse : arrayList) {

											if(parentResponse.kid_id.equals(current.kid_id)) {

												parentResponse.kid_active = ActiveUtils.APPROVED;
												break;
											}
										}

										User.updateKidConnect(current.kid_id);
										updateClassPendingParents();
										MainScreenFragment.updateContactTabBadge();
										MainActivity.updateDrawerContent();
										EventBus.getDefault().postSticky(new RefreshDrawerEvent());
									} else {

										//TODO implement active kid false
									}
								}
							}

							@Override
							public void onFailure(Call<SuccessAnswer> call, Throwable t) {

								progress.hide();

								CustomToast.show((Activity) context, R.string.error_activate_kid);

								Log.e(TAG, "onFailure: error while active kid = " + Log.getStackTraceString(t));
							}
						});
					}
				});

				holder.more_btn.setOnClickListener(new OnClickListener() {
					  @Override
					  public void onClick(View v) {
					  openMoreMenu(current.kid_id,current.class_id,current.kid_name);
					  }
				});
				
				holder.btn.setVisibility(View.VISIBLE);
				holder.more_btn.setVisibility(View.VISIBLE);

				holder.btn_ed.setVisibility(View.GONE);
			}
			else
			{
				holder.btn.setVisibility(View.GONE);
				holder.more_btn.setVisibility(View.GONE);
				holder.btn_ed.setVisibility(View.GONE);
			}
		}
 
		return view; 
	}

	private void editKidClick(ViewHolder holder, final GetParentAnswer current) {
		holder.kidDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AddKidActivity.class);
				intent.putExtra("id", current.getKid_id());
				intent.putExtra("name", current.getKid_name());
				intent.putExtra("birth_date", current.getKid_bd());
				intent.putExtra("gender", current.getKid_gender());
				intent.putExtra("pic", current.getKid_pic());
				intent.putExtra("className", current.getClass_name());
				intent.putExtra("ganName", User.current.getCurrentGanName());
				intent.putExtra("parent_id", current.parent_id);
				intent.putExtra("parent_firstName", current.parent_first_name);
				intent.putExtra("parent_lastName", current.parent_last_name);
				intent.putExtra("parent_mobile", current.parent_mobile);
				intent.putExtra("parent_phone", current.parent_phone);
				intent.putExtra("parent_address", current.parent_address);
				intent.putExtra("parent_city", current.parent_city);
				context.startActivity(intent);

			}
		});
	}

	private void updateKidAttendance(String kidId, String ganId, String attendance) {
		Call<SuccessAnswer> call = ganbookApiInterfacePOST.updateKidAttendance(kidId, ganId, attendance);

		call.enqueue(new Callback<SuccessAnswer>() {
			@Override
			public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
				SuccessAnswer answer = response.body();

				if (answer != null && answer.isSuccess()) {
					Log.d("ANSWER", answer.toString());
				}
			}

			@Override
			public void onFailure(Call<SuccessAnswer> call, Throwable t) {
				Log.d("FAIL", t.toString());
			}
		});
	}

	protected void openMoreMenu(final String kid_id, final String class_id, final String kid_name) {

		String[] items = new String[2];
		items[0] = context.getResources().getString(R.string.remove_user);
		items[1] = context.getResources().getString(R.string.change_class);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {

					if (index == 0) {
						doRemove(kid_id);
					} else {

						doChangeClass(kid_id, class_id, kid_name);
					}

			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public void doChangeClass(String kid_id,String class_id, String kid_name) {
		MyApp.gan_name = User.current.getCurrentGanName();
		MyApp.class_ids = new String[User.current.classList.classes.length - 1];
		MyApp.class_names = new String[User.current.classList.classes.length - 1];

		int ind = 0;

		for (ClassDetails_2 classDetails : User.current.classList.classes) {

			if(!classDetails.class_id.equals(class_id)) {
				MyApp.class_ids[ind] = classDetails.class_id;
				MyApp.class_names[ind] = classDetails.class_name;
				ind++;
			}

		}


		MyApp.sendAnalytics("change-class-ui", "change-class-ui-"+User.getId(), "change-class-ui", "ChangeClass");


		Intent intent = new Intent(context, ChooseClassActivity.class);

		intent.putExtra(Const.FROM_CONTACT_LIST, true);
		intent.putExtra(Const.KID_ID, kid_id);
		intent.putExtra(Const.KID_NAME, kid_name);

		context.startActivity(intent);
	}

	public void  updateContactListAfterDisconnect(String kid_id) {
		updateClassPendingParents();
		MainScreenFragment.updateContactTabBadge();
		MainActivity.updateDrawerContent();

		for(GetParentAnswer response : arrayList) {

			Log.d(TAG, "updateContactListAfterDisconnect: response = " + response
			+ "kid id = " + kid_id);
			if(response.kid_id.equals(kid_id)) {
				arrayList.remove(response);
				break;
			}
		}
		EventBus.getDefault().postSticky(new RefreshDrawerEvent());
		notifyDataSetChanged();
	}

	@Override
	public void doRemove(String kid_id)
	{
		openDisconnectPopup(kid_id);
	}

	private void openDisconnectPopup(final String kid_id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		//		adb.setView(alertDialogView);
		adb.setTitle(R.string.disconnect_text_teacher);
		adb.setIcon(R.drawable.ic_launcher);
		adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				performDisconnect(kid_id);
			} });
		adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// no op
			} });

		adb.show();
	}

	private void performDisconnect(final String kid_id) {

		Call<SuccessAnswer> updateClass = ganbookApiInterfacePOST.updateClass(kid_id, "");

		progress.show();

		updateClass.enqueue(new Callback<SuccessAnswer>() {
			@Override
			public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

				SuccessAnswer successAnswer = response.body();

				progress.hide();

				if (successAnswer != null) {

					Log.d(TAG, "onResponse: update class = " + successAnswer);

					if (successAnswer.isSuccess()) {

						if(User.isParent())
							User.updateKidDisconnect(kid_id);

						updateContactListAfterDisconnect(kid_id);

						contactsFragmentInterface.setTotalContacts(getCount());
					} else {

						//unsucces
					}
				}
			}

			@Override
			public void onFailure(Call<SuccessAnswer> call, Throwable t) {

				Log.e(TAG, "onFailure: error while update class = " + Log.getStackTraceString(t));

				progress.hide();
			}
		});

	}
	
	private void updateClassPendingParents()
	{
		User.current.updatePendingParents(null);
	}
	 
	private DisplayImageOptions uilOptions;
	
	private void getKidPicture(GetParentAnswer current, String kid_pic,
							   CircleImageView imgView, int defImageResId) {
		String url = kidPicToUrl(kid_pic);
		if (uilOptions==null) {
			uilOptions = UILManager.createDefaultDisplayOpyions(defImageResId);
		} 
		UILManager.imageLoader.displayImage(url, imgView, uilOptions); 

	}

	public static String kidPicToUrl(String kid_pic) {
		return JsonTransmitter.PICTURE_HOST + JsonTransmitter.USERS_HOST + kid_pic + ".png";
	}

	private class ValueFilter extends Filter {

		//Invoked in a worker thread to filter the data according to the constraint.
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			FilterResults results=new FilterResults();
			if(constraint!=null && constraint.length()>0){

				String filter = constraint.toString().toLowerCase(Locale.getDefault()).trim();
				ArrayList<GetParentAnswer> filterList=new ArrayList<GetParentAnswer>();

				for (GetParentAnswer response: orig_arrayList) {
					String _name = response.kid_name.toLowerCase(Locale.getDefault());
					if (_name.contains(filter)) {
						filterList.add(response);
					}
				}

				results.count=filterList.size();
				results.values=filterList;

			}else{
				results.count=orig_arrayList.size();
				results.values=orig_arrayList;
			}
			return results;
		}


		//Invoked in the UI thread to publish the filtering results in the user interface.
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
									  FilterResults results) {
			arrayList=(ArrayList<GetParentAnswer>) results.values;
			if (arrayList != null)
			notifyDataSetChanged();
		}
	}

}
