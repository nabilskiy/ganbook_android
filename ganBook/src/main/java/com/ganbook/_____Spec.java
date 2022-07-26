


/**
Startup logic: from Entry screen
=======================================
    perform general_init_logic
           check if GooglePlay services accessable
           create GCM in stance
           get CGM registration id from local cahche or, if none --
               perform GCM registration
               call createtoken() -- ONLE PER APP LIFE
               updateToken -- EVERY TIME AFTER RESTART
               and store the result.token_id as registyration id
   if logged in --
      call create_session()
      go directly to main screen



Gone
=====
  "loginmigrateandroid" GONE
  "createpicture" GONE

  
Need Impl
==============  
  "createpictures" 
  "addclass" 
  "getkindergarten"
  "createkindergarten"  
  "updatekindergarten" 
-----------------------

Noa: 077 7080962


createsession automatically as part of init logic
createuser.json -- Signup page onClick
   AFTER regisfdation perform silent loginnew
loginnew -- login page onClick

getkindergarten -- ONLY FOR parents after user inserts CODE

setclass("11556", "2074"); -- called for PARENT at end of SIGNUP procedure

getclass("1000378247");  -- for TEACHER: automatically called after teacher login +
                             also called at sideBar pullToRefresh

getuserkids  -- for TEACHER: automatically called after teacher login +
                             also called at sideBar pullToRefresh

send_getalbum("2074", "2015"); --
                      called FIRST TIME on entering (multiple) albums tab
                      and also after pullToRefresh

getpicture("11593" , "3"); -- called EACH TIME entering the (single) album View

updateAlbumView("11001", "2015", "5"); -- called after getpicture return success

getmessage("2074", "2015"); -- silently called FIRST TIME
                   entering the messages tab + PUTT-TO-REFRESH

updatereadmessage("12023", "2074", "15808"); ---
   call after getmessage succeeds with the last msg id BEFORE getmessage was called


userLogout("2092"); -- USER INITIATED LOGOUT


getparents("2074", "2015") ; -- every time upon entering contact List

createkindergarten -- Silently called after TEACHER signup
      Flow: (1) creteUser (2) create createkindergarten  (3) silent login


resetpassword("noanika@gmail.com"); -- onClick forget password page


updatekindergarten -- on settings > after change kindergarden settings

createmessage("bla bla", "2074"); -- upon creation of new message

activekid -- for TEACHER after confirmatin of child


createalbum("myalbum", "2074"); -- ONLY TACHER upon creation of new Album

deletealbumj("11001"); -- ONLY TACHER  upon deletion of new Album

editalbum("17389", "MyAlbum2"); -- ONLY TACHER upon change Album title

File upload:
 1. select from gallery/camera
 2. show preview to user
 3. allow user to scroll and delete if not needed
 4. click upload
 5. for each
        createpicture -- for each file in the fileUpload procedure
        
 send picts via ftp;

deletepicturesj("1546122.jpg,15462.jpg"); --
  TEACHER ONLY call Album View > Picture delete
  
<< for Displayed data: force view to update after sucess code server>>

addclass -- TEACHER only SideBar > Add Class

createtoken -- part of the init procedure

updatetoken -- part of the init procedure

createsession -- part of the init procedure


userLogout -- user requests to logout; Will jump to "login" screen


uploadpic(file, id, tmb, name);  -- PART OF PARENT SIGNUP ; ONLY FOR CHILD PICTURE


pushafterupload("1", "1", "17389", "2074", "0"); -- TEACHER ONLY after entire
    upload procedure is completed; send summary to server


getparentsclass("2074"); > TEACHER ONLY > SINGN VAAD




and go over all below
===========================

retention("noanika@gmail.com", "2902"); -- Enter mail screen (one before registration)

reportsavedpicture("492587");   -- After user SAVES image (should also write it to 
   a "GanBook Images" Album)



changepassword("noanika@gmail.com"); -- NOT USED
updatepassword("111111", "noa1981", "he_IL", "12023"); -- settings > update password


createkid("2095", "aa", "12023", ---- PARENT > SIDE MENU > aDD KID

updateclass("11556", "2074"); > update kid's class from contact list

getactivekids("2074"); > NOT USED

deletemessage("13713"); -- delete message

deletetoken("123"); > not used


getreadmessage("13282", "2074"); --- FOR teacher -- who read/not read a specific message

senduserspush("12023", "15808", "2074"); =-=== from read/NOt screen -- resend command will set this one

setparentpermission(parent_ids, "2074"); : TEACHWER > SIDEBAR > PARENT PERMISSION > ASSIGN


updateparent("12023", --- PARENT > SETTINGS > UPDATE DETAILS


------------
***/