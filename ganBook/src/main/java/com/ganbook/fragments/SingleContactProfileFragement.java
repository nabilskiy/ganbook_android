package com.ganbook.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ganbook.activities.MainActivity;
import com.ganbook.app.MyApp;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.ListUpdaresInterface;
import com.ganbook.interfaces.TitleIteractionListener;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.services.UploadTeacherImageService;
import com.ganbook.share.ShareManager;
import com.ganbook.ui.CircleImageView;
import com.ganbook.ui.ContactListAdapter;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.StrUtils;
import com.ganbook.validate.Validator;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class SingleContactProfileFragement extends Fragment implements OnClickListener {
	private static final String ARG_PARENT = "parent";
	public static final String TAG = SingleContactProfileFragement.class.getName();

//	contact_profile_fragment
		
	private GetParentAnswer selContact;

	private TextView contact_name,contact_class, mobile_number,school_num, email_id, set_adddess_here, set_city_here, school_text;
//	private TextView title;
	
	private ImageButton send_sms ;
	private ImageButton send_whatsapp ;
	private ImageButton make_call ;
	private ImageButton kindergarden_phone ;
	private ImageButton send_email ;
	private ImageButton show_address ;
	private CircleImageView profile_image, teacherPersonalPhoto;
	private int defImgRes;
	TitleIteractionListener titleIteractionListener;
	private DisplayImageOptions uilOptions;
	private ListUpdaresInterface listUpdaresInterface;
	private static final int LOAD_IMAGE_RESULTS = 2;
	private String teacherPhotoName;
	private Switch staffCameraAllow;
	public String cameraPerm;
	private RelativeLayout permissionLayout;

	@Inject
	@Named("GET")
	GanbookApiInterface ganbookApiInterfaceGET;

	@Inject
	@Named("POST")
	GanbookApiInterface ganbookApiInterfacePOST;

	public static SingleContactProfileFragement newInstance(GetParentAnswer getParentAnswer) {
		Bundle args = new Bundle();

		SingleContactProfileFragement fragment = new SingleContactProfileFragement();

		args.putParcelable(ARG_PARENT, getParentAnswer);

		fragment.setArguments(args);

		return fragment;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getView().setFocusableInTouchMode(true);
		getView().setOnKeyListener( new OnKeyListener() {
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					ContactListFragment.call_handleFragmentVisible();		
				}
				return false;
			}
		});
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		if (getArguments() != null) {
			((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);

			selContact = getArguments().getParcelable(ARG_PARENT);

			Log.d(TAG, "onCreate: received parent in profile = " + selContact);
			if(selContact.getVaad_type() == null) {
				teacherPhotoName = selContact.teacherPhoto;
			}
		}


	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.single_album_menu, menu);

		final MenuItem menuItem = menu.findItem(R.id.edit_menu);

		if (User.isTeacher()) {

			menuItem.getActionView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				openSingleContactMenu();
				}
			});
		} else {

			menuItem.setVisible(false);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);


		MyApp.sendAnalytics("contact-gan-ui", "contact-gan"+User.current.getCurrentGanId()+"-class"+User.current.getCurrentClassId()+"-contact"+selContact.kid_id+"-ui"+ User.getId(), "contact-gan-ui", "ContactGan");


		View _view = inflater.inflate(R.layout.contact_profile_fragment, container, false); 
		
		profile_image = (CircleImageView) _view.findViewById(R.id.profile_image);
		teacherPersonalPhoto = _view.findViewById(R.id.teacher_personal_photo);
		staffCameraAllow = _view.findViewById(R.id.staff_camera_switcher);
		permissionLayout = _view.findViewById(R.id.permissionLayout);
		if(teacherPhotoName != null) {
			Picasso.with(getActivity()).load("http://s3.ganbook.co.il/ImageStore/users/" + teacherPhotoName)
					.centerCrop()
					.fit()
					.into(teacherPersonalPhoto);
		} else {

			Picasso.with(getActivity()).load(R.drawable.teacher_profile_image)
					.centerCrop()
					.fit()
					.into(teacherPersonalPhoto);
		}

		if(User.isTeacher() ) {
			if(selContact.getVaad_type() == null) {
				teacherPersonalPhoto.setVisibility(View.VISIBLE);
				profile_image.setVisibility(View.GONE);
				staffCameraAllow.setVisibility(View.GONE);
				permissionLayout.setVisibility(View.GONE);

				teacherPersonalPhoto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(photoPickerIntent, LOAD_IMAGE_RESULTS);
					}
				});
			} else if(selContact.getType().equals(User.Type_Staff)) {
				permissionLayout.setVisibility(View.VISIBLE);
				teacherPersonalPhoto.setVisibility(View.GONE);
				staffCameraAllow.setVisibility(View.VISIBLE);
				profile_image.setVisibility(View.VISIBLE);
				getCamerraPermission();
				staffCameraAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						switch (buttonView.getId()) {
							case R.id.staff_camera_switcher:
								if (isChecked) {
									updateStaffCameraPermission(selContact.getParent_id(), "1");
								} else {
									updateStaffCameraPermission(selContact.getParent_id(), "0");

								}
						}
					}
				});
			} else {
				teacherPersonalPhoto.setVisibility(View.GONE);
				staffCameraAllow.setVisibility(View.GONE);
				profile_image.setVisibility(View.VISIBLE);
				permissionLayout.setVisibility(View.GONE);
			}

		} else {
			permissionLayout.setVisibility(View.GONE);
			staffCameraAllow.setVisibility(View.GONE);
			if(selContact.getVaad_type() == null) {
				teacherPersonalPhoto.setVisibility(View.VISIBLE);
				profile_image.setVisibility(View.GONE);
			} else {
				teacherPersonalPhoto.setVisibility(View.GONE);
				profile_image.setVisibility(View.VISIBLE);
			}

		}

		send_sms = __listen((ImageButton) _view.findViewById(R.id.send_sms));
		send_whatsapp = __listen((ImageButton) _view.findViewById(R.id.send_whatsapp));
		make_call = __listen((ImageButton) _view.findViewById(R.id.make_call));
		kindergarden_phone = __listen((ImageButton) _view.findViewById(R.id.kindergarden_phone));
		send_email = __listen((ImageButton) _view.findViewById(R.id.send_email));
		show_address = __listen((ImageButton) _view.findViewById(R.id.show_address));
				
		contact_name = (TextView) _view.findViewById(R.id.contact_name);
		contact_class = (TextView) _view.findViewById(R.id.contact_class); 
		mobile_number = (TextView) _view.findViewById(R.id.mobile_number);
		school_num = (TextView) _view.findViewById(R.id.school_num);
		email_id = (TextView) _view.findViewById(R.id.email_id);
		set_adddess_here = (TextView) _view.findViewById(R.id.set_adddess_here);
		set_city_here = (TextView) _view.findViewById(R.id.set_city_here);
		school_text = (TextView) _view.findViewById(R.id.school_text);
		
		defImgRes = selContact.getDeafultKidImg();
		
		String kid_pic = selContact.kid_pic;
		if (StrUtils.notEmpty(kid_pic)) {
			getKidPicture(selContact, kid_pic, profile_image, defImgRes);
		} else {
			profile_image.setImageResource(defImgRes);
		}

		
		contact_name.setText(selContact.kid_name); 
		contact_class.setText(getName()); 
		mobile_number.setText(selContact.parent_mobile);
		school_num.setText(selContact.parent_phone);
		email_id.setText(selContact.parent_mail);
		set_adddess_here.setText(selContact.parent_address);
		set_city_here.setText(selContact.parent_city);
		
		if(selContact.kid_gender == null)
		{
			school_text.setText(getString(R.string.k_num));
		}

		titleIteractionListener.setTitle((selContact==null ? "" : selContact.getName()));


		return _view;
	}


	private void updateStaffCameraPermission(String staffId, String cameraPermission) {
		Call<SuccessAnswer> call = ganbookApiInterfacePOST.updateStaffPermission(staffId, cameraPermission, User.current.getCurrentClassId(), User.current.getCurrentGanId());

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

	private void getCamerraPermission() {
        Call<ResponseBody> cameraPermission = ganbookApiInterfaceGET.getStaffPermissions(selContact.getParent_id(), User.current.getCurrentClassId(), User.current.getCurrentGanId());

        cameraPermission.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
					String responseBody = response.body().string();

					try {
						JSONObject cameraPermissionObject = new JSONObject(responseBody);
						cameraPerm = cameraPermissionObject.getString("camera_permission");
						Log.d("CAMERA PERMISSION", cameraPerm);
						if(cameraPerm.equals("1")) {
							staffCameraAllow.setChecked(true);
						} else {
							staffCameraAllow.setChecked(false);
						}

					} catch (JSONException e) {

					}


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                Log.e(TAG, "onFailure: error while load parents = " + Log.getStackTraceString(t));
            }
        });
    }

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	/* TEACHER PERSONAL PHOTO */

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (resultCode == RESULT_OK && reqCode == LOAD_IMAGE_RESULTS) {
			final Uri imageUri = data.getData();

			Picasso.with(getActivity()).load(imageUri)
					.centerCrop()
					.fit()
					.into(teacherPersonalPhoto);
			String logoPath = getRealPathFromURI(imageUri, getActivity());
			String logoName = String.valueOf(System.currentTimeMillis());

			Intent intent = new Intent(getActivity(), UploadTeacherImageService.class);
			intent.putExtra(UploadTeacherImageService.EXTRA_TEACHER_IMAGE_NAME, logoName);
			intent.putExtra(UploadTeacherImageService.EXTRA_TEACHER_IMAGE_PATH, logoPath);
			getActivity().startService(intent);
		}
	}

	private String getRealPathFromURI(Uri uri, Context cnt) {
		String result = "";

		Cursor cursor = cnt.getContentResolver().query(uri, null, null, null,
				null);
		if (cursor == null) {
			result = uri.getPath();
			System.out.println(result);
		}
		else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
		}
		return result;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof MainActivity) {

			titleIteractionListener = (TitleIteractionListener) context;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		titleIteractionListener = null;
		listUpdaresInterface = null;
	}

	private void openSingleContactMenu()
	{
		String[] items = new String[1];

		if(User.current.classList.classes.length > 1) {
			items = new String[2];
		}

		items[0] = getString(R.string.remove_user);

		if(User.current.classList.classes.length > 1) {
			items[1] = getString(R.string.change_class);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("");
		builder.setItems(items,  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {

				if (index == 0) {
					listUpdaresInterface.doRemove(selContact.kid_id);

					FragmentUtils.popBackStack(getContext());
				} else {
					listUpdaresInterface.doChangeClass(selContact.kid_id, selContact.class_id, selContact.kid_name);
				}

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void getKidPicture(GetParentAnswer current, String kid_pic,
							   CircleImageView imgView, int defImageResId) {
		String url = ContactListAdapter.kidPicToUrl(kid_pic);
		if (uilOptions==null) {
			uilOptions = UILManager.createDefaultDisplayOpyions(defImageResId);
		}
		UILManager.imageLoader.displayImage(url, imgView, uilOptions);
	}

	private String getName() {
		String first = selContact.parent_first_name;
		String last = selContact.parent_last_name;
		return StrUtils.emptyIfNull(first) + " "  + StrUtils.emptyIfNull(last); 
	}


	private ImageButton __listen(ImageButton btn) {
		if (btn != null) {
			btn.setOnClickListener(this);
		}
		return btn;
	}
	
	@Override
	public void onClick(View v) {
		int jj=234;
		jj++;
		String mobile = selContact.parent_mobile;
		String phone = selContact.parent_phone;
		String email = selContact.parent_mail;
		switch (v.getId()) {
		case R.id.send_sms:
			sendSms(mobile); 
			break;
		case R.id.send_whatsapp:
			sendWhatsapp(mobile);
			break;
		case R.id.make_call:
			makeCall(mobile); 
			break;
		case R.id.kindergarden_phone:
			makeCall(phone);
			break;
		case R.id.send_email:
			sendEmail(email);
			break;
		case R.id.show_address:
			// no op
			break;
		default:
		}
	}
	
	private void sendEmail(String emailAddress) {
//		ShareManager.openShareMenu(getActivity(), -1, -1);
		ShareManager.openShareMenu(getActivity(), -1, -1, "", emailAddress, null);
	}
	
	private void makeCall(String phoneNo) {
		if (!Validator.validPhoneNo(phoneNo)) {
			return;
		}
		phoneNo = phoneNo.trim();
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phoneNo));		
		getActivity().startActivity(intent); 
	}
	
	private void sendSms(String phoneNo) {
		if (!Validator.validPhoneNo(phoneNo)) {
			return;
		}
		phoneNo = phoneNo.trim();
		String smsText = "";
        String uri= "smsto:" + phoneNo;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        intent.putExtra("sms_body", smsText);
        intent.putExtra("compose_mode", true);
        getActivity().startActivity(intent);
	}

	private void sendWhatsapp(String phoneNo) {
		if (!Validator.validPhoneNo(phoneNo)) {
			return;
		}
		phoneNo = phoneNo.trim();

		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNo));

		i.setType("text/plain");
		i.setPackage("com.whatsapp");           // so that only Whatsapp reacts and not the chooser
//		i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//		i.putExtra(Intent.EXTRA_TEXT, "I'm the body.");
		startActivity(i);

	}

	public void setListUpdaresInterface(ListUpdaresInterface listUpdaresInterface) {
		this.listUpdaresInterface = listUpdaresInterface;
	}
}
