package com.android.sigemesapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class TransactionStatusResponse(

	@field:SerializedName("status_message")
	val statusMessage: String,

	@field:SerializedName("transaction_id")
	val transactionId: String,

	@field:SerializedName("fraud_status")
	val fraudStatus: String,

	@field:SerializedName("approval_code")
	val approvalCode: String,

	@field:SerializedName("transaction_status")
	val transactionStatus: String,

	@field:SerializedName("status_code")
	val statusCode: String,

	@field:SerializedName("reference_id")
	val referenceId: String,

	@field:SerializedName("signature_key")
	val signatureKey: String,

	@field:SerializedName("payment_option_type")
	val paymentOptionType: String,

	@field:SerializedName("gross_amount")
	val grossAmount: String,

	@field:SerializedName("card_type")
	val cardType: String,

	@field:SerializedName("shopeepay_reference_number")
	val shopeepayReferenceNumber: String,

	@field:SerializedName("payment_type")
	val paymentType: String,

	@field:SerializedName("bank")
	val bank: String,

	@field:SerializedName("masked_card")
	val maskedCard: String,

	@field:SerializedName("transaction_time")
	val transactionTime: String,

	@field:SerializedName("channel_response_code")
	val channelResponseCode: String,

	@field:SerializedName("order_id")
	val orderId: String,

	@field:SerializedName("channel_response_message")
	val channelResponseMessage: String
)
