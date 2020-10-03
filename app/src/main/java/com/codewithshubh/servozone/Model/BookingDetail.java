package com.codewithshubh.servozone.Model;

public class BookingDetail {
    private String bookingIdParent, bookingID, serviceId, serviceCategoryId, serviceProvider, contactPerson, bookingAddress,
            contactNumber, dateOfBooking, timeOfBooking, serviceDateSlot, serviceTimeSlot, query, userUID, userEmail, userMobile,
            bookingStatus, serviceManName, serviceManContact, bookingImgUrl, bookingFinalDate, bookingConfirmDate, bookingCharge, smAssignedDate;
    private long bookingTimeStamp;

    public BookingDetail() {
    }

    public BookingDetail(String bookingIdParent, String bookingID, String serviceId, String serviceCategoryId, String serviceProvider, String contactPerson,
                         String bookingAddress, String contactNumber, String dateOfBooking, String timeOfBooking, String serivceDateSlot,
                         String serviceTimeSlot,
                         String query, String userUID, String userEmail, String userMobile, String bookingStatus, String serviceManName,
                         String serviceManContact, String bookingImgUrl, String bookingFinalDate, String bookingConfirmDate, String bookingCharge,
                         String smAssignedDate) {
        this.bookingIdParent = bookingIdParent;
        this.bookingID = bookingID;
        this.serviceId = serviceId;
        this.serviceCategoryId = serviceCategoryId;
        this.serviceProvider = serviceProvider;
        this.contactPerson = contactPerson;
        this.bookingAddress = bookingAddress;
        this.contactNumber = contactNumber;
        this.dateOfBooking = dateOfBooking;
        this.timeOfBooking = timeOfBooking;
        this.serviceDateSlot = serivceDateSlot;
        this.serviceTimeSlot = serviceTimeSlot;
        this.query = query;
        this.userUID = userUID;
        this.userEmail = userEmail;
        this.userMobile = userMobile;
        this.bookingStatus = bookingStatus;
        this.serviceManName = serviceManName;
        this.serviceManContact = serviceManContact;
        this.bookingImgUrl = bookingImgUrl;
        this.bookingFinalDate = bookingFinalDate;
        this.bookingConfirmDate = bookingConfirmDate;
        this.bookingCharge = bookingCharge;
        this.smAssignedDate = smAssignedDate;
    }

    public String getBookingIdParent() {
        return bookingIdParent;
    }

    public void setBookingIdParent(String bookingIdParent) {
        this.bookingIdParent = bookingIdParent;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCategoryId() {
        return serviceCategoryId;
    }

    public void setServiceCategoryId(String serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getBookingAddress() {
        return bookingAddress;
    }

    public void setBookingAddress(String bookingAddress) {
        this.bookingAddress = bookingAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDateOfBooking() {
        return dateOfBooking;
    }

    public void setDateOfBooking(String dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    public String getTimeOfBooking() {
        return timeOfBooking;
    }

    public void setTimeOfBooking(String timeOfBooking) {
        this.timeOfBooking = timeOfBooking;
    }

    public String getServiceDateSlot() {
        return serviceDateSlot;
    }

    public void setServiceDateSlot(String serivceDateSlot) {
        this.serviceDateSlot = serivceDateSlot;
    }

    public String getServiceTimeSlot() {
        return serviceTimeSlot;
    }

    public void setServiceTimeSlot(String serviceTimeSlot) {
        this.serviceTimeSlot = serviceTimeSlot;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getServiceManName() {
        return serviceManName;
    }

    public void setServiceManName(String serviceManName) {
        this.serviceManName = serviceManName;
    }

    public String getServiceManContact() {
        return serviceManContact;
    }

    public void setServiceManContact(String serviceManContact) {
        this.serviceManContact = serviceManContact;
    }

    public String getBookingImgUrl() {
        return bookingImgUrl;
    }

    public void setBookingImgUrl(String bookingImgUrl) {
        this.bookingImgUrl = bookingImgUrl;
    }

    public String getBookingFinalDate() {
        return bookingFinalDate;
    }

    public void setBookingFinalDate(String bookingFinalDate) {
        this.bookingFinalDate = bookingFinalDate;
    }

    public String getBookingConfirmDate() {
        return bookingConfirmDate;
    }

    public void setBookingConfirmDate(String bookingConfirmDate) {
        this.bookingConfirmDate = bookingConfirmDate;
    }

    public String getBookingCharge() {
        return bookingCharge;
    }

    public void setBookingCharge(String bookingCharge) {
        this.bookingCharge = bookingCharge;
    }

    public long getBookingTimeStamp() {
        return bookingTimeStamp;
    }

    public void setBookingTimeStamp(long bookingTimeStamp) {
        this.bookingTimeStamp = bookingTimeStamp;
    }

    public String getSmAssignedDate() {
        return smAssignedDate;
    }

    public void setSmAssignedDate(String smAssignedDate) {
        this.smAssignedDate = smAssignedDate;
    }
}


