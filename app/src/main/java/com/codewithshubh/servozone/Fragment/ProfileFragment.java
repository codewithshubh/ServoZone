package com.codewithshubh.servozone.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codewithshubh.servozone.Activity.EditProfileActivity;
import com.codewithshubh.servozone.Activity.ImagePickerActivity;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.Activity.MyAddressActivity;
import com.codewithshubh.servozone.Activity.MyNotificationActivity;
import com.codewithshubh.servozone.Model.User;
import com.codewithshubh.servozone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private String whatsappNumber, messengerID;
    private TextView toolbar_title, tv_name, tv_number, tv_email;
    private AlertDialog dialog;
    private CircleImageView iv_profile;
    private ImageView iv_edit, iv_upload;
    private RelativeLayout rl_notification, rl_address, rl_booking, rl_chat, rl_help, rl_rateus, rl_share, rl_aboutus,
    rl_terms, rl_privacy, rl_app, rl_logout;
    private String parentNode;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static final int REQUEST_IMAGE = 100;
    private FirebaseAuth mAuth;
    private ReviewInfo reviewInfo;
    private String profileUrl;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Initialize view
        Init(view);

        SetData();

        //On logout click
        rl_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnLogoutClick();
            }
        });
        
        //On App detail click
        rl_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnAppClick();
            }
        });

        //on privacy policy click
        rl_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnPrivacyClick();
            }
        });
        
        //On terms and conditions click
        rl_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnTermsClick();
            }
        });
        
        //On about us click
        rl_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnAboutusClick();
            }
        });

        // On share app click
        rl_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnShareClick();
            }
        });
        
        //On rate us click
        rl_rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRateusClick();
            }
        });
        
        //on help & faq click
        rl_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnHelpClick();
            }
        });
        
        //on chat and support click
        rl_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnChatClick(v);
            }
        });
        
        //On my bookings click
        rl_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBookingClick();
            }
        });
        
        //On my address click
        rl_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnAddressClick();
            }
        });
        
        //On my notifications click
        rl_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnNotificationClick();
            }
        });
        
        //On edit profile click
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnEditClick();
            }
        });
        
        //On profile image upload click
        iv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnUploadClick();
            }
        });
        
        return view;
    }

    private void SetData() {
        dialog.show();
        String uid = mAuth.getUid();
        final DatabaseReference myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tv_number.setText(user.getPhone());
                tv_name.setText(user.getName());
                tv_email.setText(user.getEmail());
                profileUrl = user.getImageUrl();
                if (!profileUrl.isEmpty() && profileUrl != null){
                    Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.user)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(iv_profile, new Callback() {
                                @Override
                                public void onSuccess() {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.user)
                                            .into(iv_profile, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    dialog.dismiss();
                                                    Picasso.get().load(R.drawable.user).into(iv_profile);
                                                    //Toasty.error(getActivity(), e.getMessage(), Toasty.LENGTH_SHORT, true).show();
                                                }
                                            });
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toasty.error(getActivity(), databaseError.getMessage(), Toasty.LENGTH_SHORT, true).show();
            }
        });


        //getting whatsapp number and messenger link from server
        DatabaseReference myRefWp = FirebaseDatabase.getInstance().getReference("ChatSupport").child("Whatsapp").child("whatsappNumber");
        DatabaseReference myRefFb = FirebaseDatabase.getInstance().getReference("ChatSupport").child("Facebook").child("messengerID");

        myRefWp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                whatsappNumber = dataSnapshot.getValue().toString();
                Log.d("ProfileFragment", "onDataChange: "+whatsappNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRefFb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messengerID = dataSnapshot.getValue().toString();
                Log.d("ProfileFragment", "onDataChange: "+messengerID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void OnChatClick(View v) {
            //init the wrapper with style
            Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupStyle);
            //init the popup
            PopupMenu popup = new PopupMenu(wrapper, v);

            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //inflate menu
            popup.getMenuInflater().inflate(R.menu.support_menu, popup.getMenu());

            //implement click events
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.support_facebook:
                            if (messengerID.isEmpty() || messengerID==null)
                                Toasty.error(getActivity(), "Something went wrong! Please try again...", Toasty.LENGTH_SHORT, true).show();
                            else {
                                Uri uri = Uri.parse(messengerID); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                //Toast.makeText(getActivity(), "Facebook clicked", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.support_whatsapp:
                            if (whatsappNumber.isEmpty() || wrapper==null){
                                Toasty.error(getActivity(), "Something went wrong! Please try again...", Toasty.LENGTH_SHORT, true).show();
                            }
                            else {
                                String url = "https://api.whatsapp.com/send?phone=" + whatsappNumber;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                            //Toast.makeText(getActivity(), "Whatsapp clicked", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }
            });
            popup.show();
    }

    private void OnUploadClick() {
        //Toasty.info(getActivity(), "Upload Profile Image", Toasty.LENGTH_SHORT, true).show();
        Dexter
                .withContext(getActivity())
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_COMPRESSION_QUALITY, 10);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_COMPRESSION_QUALITY, 10);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);


        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                UploadProfilePic(uri);
            }
        }
    }

    private void UploadProfilePic(Uri uri) {

        //show progress
        dialog.show();

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://topline-service.appspot.com").child("UserProfiles")
                .child(mAuth.getUid()).child("profile_pic.jpg");
        UploadTask uploadTask = storageReference.putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                com.google.android.gms.tasks.Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());

                Uri downloadUri = uriTask.getResult();
                //Check if image is uploaded or not and url is received
                String uid = mAuth.getUid();
                if (uriTask.isSuccessful()){
                    HashMap<String, Object> uploadedPic = new HashMap<>();
                    uploadedPic.put("imageUrl", downloadUri.toString());
                    DatabaseReference myRefProfilePic = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    myRefProfilePic.updateChildren(uploadedPic).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            SetData();
                            Toasty.success(getActivity(), "Profile pic uploaded successfully", Toasty.LENGTH_SHORT, true).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(getActivity(), e.getMessage(), Toasty.LENGTH_SHORT, true).show();
                            dialog.dismiss();
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(getActivity(), e.getMessage(), Toasty.LENGTH_SHORT, true).show();
                dialog.dismiss();
            }
        });
    }

    private void OnEditClick() {
        Intent i = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(i);
    }

    private void OnNotificationClick() {
        startActivity(new Intent(getActivity(), MyNotificationActivity.class));
    }

    private void OnAddressClick() {
        startActivity(new Intent(getActivity(), MyAddressActivity.class));
    }

    private void OnBookingClick() {
        BookingFragment nextFrag= new BookingFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, nextFrag, "findThisFragment")
                .commit();
        ((MainActivity)getActivity()).bnv.setSelectedItemId(R.id.bottom_navbar_booking);
    }

    private void OnHelpClick() {
        HelpFragment nextFrag= new HelpFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, nextFrag, "findThisFragment")
                .commit();
        ((MainActivity)getActivity()).bnv.setSelectedItemId(R.id.bottom_navbar_help);
    }

    private void OnRateusClick() {
        ReviewManager manager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
            } else {
                // There was some problem, continue regardless of the result.
            }
        });
        if (reviewInfo!=null) {
            Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
            flow.addOnCompleteListener(task -> {
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
            });
        }
        //Toasty.info(getActivity(), "Yet to be uploaded on playstore.", Toasty.LENGTH_SHORT, true).show();
    }

    private void OnShareClick() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String shareMessage= getResources().getString(R.string.share_msg);
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getActivity().getApplicationContext().getPackageName();;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share using"));
        }
        catch(Exception e) {
            //e.toString();
        }
    }

    private void OnAboutusClick() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.webview_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("About Us");
        alertDialogBuilder.setView(promptsView);
        final WebView webview = (WebView) promptsView
                .findViewById(R.id.webview_layout);
        webview.loadUrl("file:///android_asset/aboutus.html");
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void OnTermsClick() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.webview_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Terms & Conditions");
        alertDialogBuilder.setView(promptsView);

        final WebView webview = (WebView) promptsView
                .findViewById(R.id.webview_layout);
        webview.loadUrl("file:///android_asset/term_and_condition.html");
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        //Toasty.info(getActivity(), "Terms & Conditions", Toasty.LENGTH_SHORT, true).show();
    }

    private void OnPrivacyClick() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.webview_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Privacy Policy");
        alertDialogBuilder.setView(promptsView);

        final WebView webview = (WebView) promptsView
                .findViewById(R.id.webview_layout);
        webview.loadUrl("file:///android_asset/privacy_policy.html");
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        //Toasty.info(getActivity(), "Privacy Policy", Toasty.LENGTH_SHORT, true).show();
    }

    private void OnAppClick() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.webview_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        alertDialogBuilder.setTitle("App details");
        alertDialogBuilder.setView(promptsView);

        final WebView webview = (WebView) promptsView
                .findViewById(R.id.webview_layout);
        webview.loadUrl("file:///android_asset/app_detail.html");
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        //Toasty.info(getActivity(), "App Details", Toasty.LENGTH_SHORT, true).show();
    }

    private void OnLogoutClick() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("Are you sure?");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.setContentText("Won't be able to book services and see your profile!")
                .setConfirmText("Yes, Logout!")
                .setCancelText("Cancel")
                .setCancelButtonBackgroundColor(ContextCompat.getColor(getActivity(),R.color.dark_gray))
                .setConfirmButtonBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        SweetAlertDialog s = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                        s.setCancelable(false);
                        s.setCanceledOnTouchOutside(false);
                        s.setTitleText("See you soon!")
                                .setContentText("You have been logged out successfully.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        mAuth.signOut();
                                        HomeFragment nextFrag= new HomeFragment();

                                        ((MainActivity)getActivity()).bnv.setSelectedItemId(R.id.bottom_navbar_home);
                                        Toasty.success(getActivity(), "Sign Out Successful",
                                                Toasty.LENGTH_SHORT, true).show();
                                        //((MainActivity)getActivity()).openFragmentWithRefresh(nextFrag);
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.container, nextFrag, "findThisFragment")
                                                .commit();

                                    }
                                })
                                .show();
                    }
                })
                .show();

    }

    private void Init(View view) {
        //setting toolbar title
        toolbar_title = ((MainActivity)getActivity()).findViewById(R.id.activity_main_toolbar_tv);
        toolbar_title.setText(getResources().getString(R.string.profile_fragment_toolbar_title));
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        iv_profile = view.findViewById(R.id.user_profile_pic);
        tv_name = view.findViewById(R.id.user_profile_name);
        tv_number = view.findViewById(R.id.user_profile_number);
        tv_email = view.findViewById(R.id.user_profile_email);

        iv_edit = view.findViewById(R.id.fragment_profile_iv_edit);
        iv_upload = view.findViewById(R.id.fragment_profile_iv_upload);

        rl_notification = view.findViewById(R.id.rl_notification);
        rl_address = view.findViewById(R.id.rl_address);
        rl_booking = view.findViewById(R.id.rl_booking);
        rl_chat = view.findViewById(R.id.rl_chat);
        rl_help = view.findViewById(R.id.rl_help);
        rl_rateus = view.findViewById(R.id.rl_rateus);
        rl_share = view.findViewById(R.id.rl_share);
        rl_aboutus = view.findViewById(R.id.rl_about);
        rl_terms = view.findViewById(R.id.rl_terms);
        rl_privacy = view.findViewById(R.id.rl_privacy);
        rl_app = view.findViewById(R.id.rl_aboutapp);
        rl_logout = view.findViewById(R.id.rl_logout);

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();

    }

}
