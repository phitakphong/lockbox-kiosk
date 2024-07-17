package com.lockboxth.lockboxkiosk.http.api

import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.http.HttpResponse
import com.lockboxth.lockboxkiosk.http.model.cp.CpParcelResponse
import com.lockboxth.lockboxkiosk.http.model.cp.CpReOpenRequest
import com.lockboxth.lockboxkiosk.http.model.locker.CancelTransactionRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutRequest
import com.lockboxth.lockboxkiosk.http.model.locker.LockerCheckoutResponse
import com.lockboxth.lockboxkiosk.http.model.locker.LockerFinishRequest
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodRequest
import com.lockboxth.lockboxkiosk.http.model.payment.PaymentMethodResponse
import com.lockboxth.lockboxkiosk.http.model.personal.ConfirmRegisterMemberRequest
import com.lockboxth.lockboxkiosk.http.model.personal.ConfirmRegisterMemberResponse
import com.lockboxth.lockboxkiosk.http.model.personal.VerifyOtpRequest
import com.lockboxth.lockboxkiosk.http.model.pudo.*
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PudoAPI {

    @POST("kiosk/pudo/sender/2.1/verify")
    fun senderVerify(
        @Body req: PudoSenderVerifyRequest
    ): Flowable<Response<HttpResponse<PudoSenderVerifyResponse>>>

    @POST("kiosk/pudo/sender/2.1/detail")
    fun senderDetail(
        @Body req: PudoSenderConfirmRequest
    ): Flowable<Response<HttpResponse<PudoParcelAddressResponse>>>

    @POST("kiosk/pudo/sender/2.1/waybill")
    fun senderWaybill(
        @Body req: PudoWayBillRequest
    ): Flowable<Response<HttpResponse<PudoWalkinWayBillResponse>>>

    @POST("kiosk/pudo/sender/2.1/scan")
    fun waybillSenderScan(
        @Body req: PudoWayBillScanRequest
    ): Flowable<Response<HttpResponse<PudoSenderConfirmResponse>>>

    @POST("kiosk/pudo/sender/identify")
    fun senderIdentify(
        @Body req: PudoVerifyIdCardRequest
    ): Flowable<Response<HttpResponse<PudoParcelAddressResponse>>>

    @POST("kiosk/pudo/sender/confirm")
    fun senderConfirm(
        @Body req: PudoSenderConfirmRequest
    ): Flowable<Response<HttpResponse<PudoSenderConfirmResponse>>>

    @POST("kiosk/pudo/sender/2.1/callback")
    fun senderCallbackSuccess(
        @Body req: PudoSenderCallbackRequest
    ): Flowable<Response<HttpResponse<PudoSenderCallbackSuccessResponse>>>

    @POST("kiosk/pudo/walkin/2.1/callback")
    fun senderWalkinCallbackSuccess(
        @Body req: PudoSenderCallbackRequest
    ): Flowable<Response<HttpResponse<PudoSenderCallbackSuccessResponse>>>

    @POST("kiosk/pudo/sender/2.1/callback")
    fun senderCallbackChangeLocker(
        @Body req: PudoSenderCallbackRequest
    ): Flowable<Response<HttpResponse<PudoSenderConfirmResponse>>>

    @POST("kiosk/pudo/walkin/2.1/callback")
    fun senderWalkinCallbackChangeLocker(
        @Body req: PudoSenderCallbackRequest
    ): Flowable<Response<HttpResponse<PudoSenderConfirmResponse>>>

    @POST("kiosk/pudo/sender/2.1/finish")
    fun senderFinish(
        @Body req: PudoSenderFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/pudo/sender/2.1/reopen")
    fun senderReOpen(
        @Body req: CpReOpenRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>

    @POST("kiosk/pudo/walkin/2.1/reopen")
    fun walkinReOpen(
        @Body req: CpReOpenRequest
    ): Flowable<Response<HttpResponse<CpParcelResponse>>>

    @POST("kiosk/pudo/walkin/2.1/finish")
    fun senderWalkinFinish(
        @Body req: PudoSenderFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/pudo/sender/2.1/finish")
    fun senderFinishChange(
        @Body req: PudoSenderFinishRequest
    ): Flowable<Response<HttpResponse<PudoSenderConfirmResponse>>>

    @POST("kiosk/pudo/walkin/2.1/finish")
    fun senderWalkinFinishChange(
        @Body req: PudoSenderFinishRequest
    ): Flowable<Response<HttpResponse<PudoSenderConfirmResponse>>>

    @POST("kiosk/pudo/walkin/2.1/personal/verify")
    fun verifyMember(
        @Body req: PudoVerifyMemberRequest
    ): Flowable<Response<HttpResponse<PudoVerifyMemberResponse>>>

    @POST("kiosk/pudo/walkin/2.1/member/confirm")
    fun requestOtp(
        @Body req: ConfirmRegisterMemberRequest
    ): Flowable<Response<HttpResponse<ConfirmRegisterMemberResponse>>>

    @POST("kiosk/pudo/walkin/2.1/member/verify")
    fun verifyOtp(
        @Body req: VerifyOtpRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/pudo/walkin/2.1/personal/identify")
    fun walkInIdentify(
        @Body req: PudoVerifyIdCardRequest
    ): Flowable<Response<HttpResponse<PudoWalkInIdentifyResponse>>>

    @POST("kiosk/pudo/walkin/2.1/parcel/consent")
    fun walkInConsent(
        @Body req: PudoWalkInConsentRequest
    ): Flowable<Response<HttpResponse<PudoWalkInConsentResponse>>>

    @POST("kiosk/pudo/courier/shipment")
    fun shipments(
        @Body req: PudoShipmentRequest
    ): Flowable<Response<HttpResponse<PudoShipmentResponse>>>

    @POST("kiosk/pudo/courier/select")
    fun selectCourier(
        @Body req: PudoCourierSelectRequest
    ): Flowable<Response<HttpResponse<PudoCourierSelectResponse>>>

    @POST("kiosk/pudo/courier/verify")
    fun verifyCourier(
        @Body req: PudoVerifyCourierRequest
    ): Flowable<Response<HttpResponse<PudoVerifyCourierResponse>>>

    @POST("kiosk/pudo/courier/status")
    fun updateStatusCourier(
        @Body req: PudoUpdateStatusCourierRequest
    ): Flowable<Response<HttpResponse<PudoVerifyCourierResponse>>>

    @POST("kiosk/pudo/courier/parcel")
    fun courierParcel(
        @Body req: PudoCourierParcelRequest
    ): Flowable<Response<HttpResponse<PudoCourierParcelResponse>>>

    @POST("kiosk/pudo/courier/callback")
    fun courierCallback(
        @Body req: PudoCourierCallbackRequest
    ): Flowable<Response<HttpResponse<PudoCourierCallbackResponse>>>

    @POST("kiosk/pudo/receiver/verify")
    fun receiverVerify(
        @Body req: PudoReceiverVerifyRequest
    ): Flowable<Response<HttpResponse<PudoReceiverVerifyResponse>>>

    @POST("kiosk/pudo/receiver/confirm")
    fun receiverConfirm(
        @Body req: PudoReceiverConfirmRequest
    ): Flowable<Response<HttpResponse<JsonObject>>>

    @POST("kiosk/pudo/receiver/payment")
    fun receiverPayment(
        @Body req: PaymentMethodRequest
    ): Flowable<Response<HttpResponse<PudoReceiverPaymentResponse>>>

    @POST("kiosk/pudo/receiver/payment/confirm")
    fun receiverPaymentConfirm(
        @Body req: PudoReceiverPaymentConfirmRequest
    ): Flowable<Response<HttpResponse<PudoReceiverPaymentConfirmResponse>>>

    @POST("kiosk/pudo/receiver/checkout")
    fun receiverCheckout(
        @Body req: PudoReceiverCheckoutRequest
    ): Flowable<Response<HttpResponse<LockerCheckoutResponse>>>

    @POST("kiosk/pudo/{path}/cancel")
    fun receiverCancel(
        @Path("path") path: String,
        @Body req: CancelTransactionRequest
    ): Flowable<Response<HttpResponse<PudoReceiverCancelResponse?>>>


    @POST("kiosk/pudo/receiver/finish")
    fun receiverFinish(
        @Body req: LockerFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

    @POST("kiosk/pudo/walkin/2.1/bankacc/add")
    fun bank(
        @Body req: PudoBankRequest
    ): Flowable<Response<HttpResponse<PudoBankResponse>>>

    @POST("kiosk/pudo/walkin/2.1/bankacc/confirm")
    fun addBank(
        @Body req: PudoAddBankRequest
    ): Flowable<Response<HttpResponse<PudoWalkInConsentResponse>>>

    @POST("kiosk/pudo/walkin/2.1/parcel/confirm")
    fun walkInParcelConfirm(
        @Body req: PudoWalkInParcelConfirmRequest
    ): Flowable<Response<HttpResponse<PudoRevieverResponse>>>

    @POST("kiosk/pudo/walkin/2.1/receiver/add")
    fun provinces(
        @Body req: PudoLocationRequest
    ): Flowable<Response<HttpResponse<PudoLocationResponse>>>

    @POST("kiosk/option/district")
    fun subDistrict(
        @Body req: PudoLocationRequest
    ): Flowable<Response<HttpResponse<PudoLocationResponse>>>

    @POST("kiosk/option/amphur")
    fun district(
        @Body req: PudoLocationRequest
    ): Flowable<Response<HttpResponse<PudoLocationResponse>>>

    @POST("kiosk/pudo/walkin/2.1/receiver/confirm")
    fun addReceiver(
        @Body req: PudoSubmitRecieverRequest
    ): Flowable<Response<HttpResponse<PudoRevieverResponse>>>

    @POST("kiosk/pudo/walkin/2.1/receiver/select")
    fun walkinReceiverSelect(
        @Body req: PudoSelectLocationTypeRequest
    ): Flowable<Response<HttpResponse<PudoSelectLocationTypeResponse>>>

    @POST("kiosk/pudo/walkin/2.1/pickup/confirm")
    fun walkinPickupConfirm(
        @Body req: JsonObject
    ): Flowable<Response<HttpResponse<PudoWalkInPickupConfirmResponse>>>

    @POST("kiosk/pudo/walkin/2.1/shipment/select")
    fun walkinShipmentSelect(
        @Body req: PudoWalkInShipmentSelectRequest
    ): Flowable<Response<HttpResponse<PudoWalkInAddressDetailResponse>>>

    @POST("kiosk/pudo/walkin/2.1/shipment/confirm")
    fun shipmentConfirm(
        @Body req: PaymentMethodRequest
    ): Flowable<Response<HttpResponse<PudoWalkinWayBillResponse>>>

    @POST("kiosk/pudo/walkin/2.1/confirm")
    fun walkinConfirm(
        @Body req: PudoReceiverPaymentConfirmRequest
    ): Flowable<Response<HttpResponse<PudoWalkInPaymentConfirmResponse>>>

    @POST("kiosk/pudo/walkin/2.1/checkout")
    fun walkinCheckout(
        @Body req: LockerCheckoutRequest
    ): Flowable<Response<HttpResponse<LockerCheckoutResponse>>>

    @POST("kiosk/pudo/waybill")
    fun waybill(
        @Body req: PudoWayBillRequest
    ): Flowable<Response<HttpResponse<PudoWayBillResponse>>>

    @POST("kiosk/pudo/walkin/2.1/waybill")
    fun waybillWalkin(
        @Body req: PudoWayBillRequest
    ): Flowable<Response<HttpResponse<PudoWalkinWayBillResponse>>>

    @POST("kiosk/pudo/walkin/2.1/scan")
    fun waybillWalkinScan(
        @Body req: PudoWayBillScanRequest
    ): Flowable<Response<HttpResponse<PudoReceiverPaymentResponse>>>

    @POST("kiosk/pudo/walkin/2.1/callback")
    fun walkInCallback(
        @Body req: LockerFinishRequest
    ): Flowable<Response<HttpResponse<Any>>>

}






