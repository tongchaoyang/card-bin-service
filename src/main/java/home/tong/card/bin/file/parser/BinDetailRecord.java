package home.tong.card.bin.file.parser;

import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import home.tong.card.bin.enums.AccountFundSource;
import home.tong.card.bin.enums.AccountFundSourceSubType;
import home.tong.card.bin.enums.CardClass;
import home.tong.card.bin.enums.DetailCardIndicator;
import home.tong.card.bin.enums.DetailCardProduct;
import home.tong.card.bin.enums.FastFunds;
import home.tong.card.bin.enums.FsaIndicator;
import home.tong.card.bin.enums.IssuingNetwork;
import home.tong.card.bin.enums.MoneySendIndicator;
import home.tong.card.bin.enums.OriginalCreditIndicator;
import home.tong.card.bin.enums.PrepaidIndicator;
import home.tong.card.bin.enums.RecordTypeIndicator;
import home.tong.card.bin.enums.RegulatorIndicator;
import home.tong.card.bin.enums.VisaLargeTicketIndicator;
import home.tong.card.bin.enums.VisaProductSubType;
import home.tong.card.bin.enums.YesNoAnswer;

import java.text.ParseException;

public class BinDetailRecord extends BinRecord {
    private static final RecordTypeIndicator RECORD_TYPE_INDICATOR = RecordTypeIndicator.DETAIL;
    private String lowBin;
    private String highBin;
    private int binLength;
    private int binDetailPan;
    private String issuerBankName;
    private CountryCode countryCode;
    private DetailCardProduct detailCardProduct = DetailCardProduct.DEFAULT;
    private DetailCardIndicator detailCardIndicator = DetailCardIndicator.DEFAULT;
    private String issuerUpdateYear;
    private String issuerUpdateMonth;
    private String issuerUpdateDay;
    private String debitNetworkPinlessIndicator;
    private String ebtState;
    private String debitSignatureNetworkParticipant;
    private FsaIndicator fsaIndicator = FsaIndicator.DEFAULT;
    private PrepaidIndicator prepaidIndicator = PrepaidIndicator.DEFAULT;
    private String productId;
    private RegulatorIndicator regulatorIndicator = RegulatorIndicator.ISS_NONREGULATED;
    private VisaProductSubType visaProductSubtype = VisaProductSubType.DEFAULT;
    private VisaLargeTicketIndicator visaLargeTicketIndicator = VisaLargeTicketIndicator.DEFAULT;
    private YesNoAnswer accountLevelProcessingIndicator = YesNoAnswer.NEITHER;
    private AccountFundSource accountFundSource = AccountFundSource.DEFAULT;
    private CardClass cardClass = CardClass.DEFAULT;
    private int panLengthMin;
    private int panLengthMax;
    private YesNoAnswer tokenIndicator = YesNoAnswer.NEITHER;
    private IssuingNetwork issuingNetwork = IssuingNetwork.DEFAULT;
    private CurrencyCode cardholderBillingCurrency;
    private AccountFundSourceSubType accountFundSourceSubType = AccountFundSourceSubType.NOT_APPLICABLE;
    private MoneySendIndicator moneySendIndicator = MoneySendIndicator.DEFAULT;
    private OriginalCreditIndicator originalCreditMoneyTransferIndicator = OriginalCreditIndicator.DEFAULT;
    private OriginalCreditIndicator originalCreditOnlineGamblingIndicator = OriginalCreditIndicator.DEFAULT;
    private FastFunds fastFunds = FastFunds.NO_PARTICIPATION;
    private OriginalCreditIndicator originalCreditTransactionIndicator = OriginalCreditIndicator.DEFAULT;

    private static final int FILLER_LENGTH_2 = 2;
    private static final int FILLER_LENGTH_4 = 4;
    private static final int FILLER_LENGTH_5 = 5;
    private static final int FILLER_LENGTH_6 = 6;
    private static final int FILLER_LENGTH_18 = 18;

    enum BinFileDetailFieldInfo implements FieldInfo {
        RECORD_TYPE("Record Type", 1),
        LOW_BIN("Low BIN", 16),
        HIGH_BIN("High BIN", 16),
        BIN_LENGTH("BIN Length", 2),
        BIN_DETAIL_PAN("BIN Detail PAN", 2),
        ISSUER_BANK_NAME("Issuer Bank Name", 60),
        COUNTRY_CODE("Country Code", 3),
        DETAIL_CARD_PRODUCT("Detail Card Product", 1),
        DETAIL_CARD_INDICATOR("Detail Card Indicator", 2),
        ISSUER_UPDATE_YEAR("Issuer Update Year", 2),
        ISSUER_UPDATE_MONTH("Issuer Update Month", 2),
        ISSUER_UPDATE_DAY("Issuer Update Day", 2),
        DEBIT_NETWORK_PINLESS_INDICATOR("Debit Network / PINless Indicator", 60),
        EBT_STATE("EBT-State", 2),
        DEBIT_SIGNATURE_NETWORK_PARTICIPANT("Debit Signature Network Participant", 2),
        FSA_INDICATOR("FSA Indicator", 1),
        PREPAID_INDICATOR("Prepaid Indicator", 1),
        PRODUCT_ID("Product ID", 3),
        REGULATOR_INDICATOR("Regulator Indicator", 1),
        VISA_PRODUCT_SUBTYPE("Visa Product Sub-Type", 2),
        VISA_LARGE_TICKET_INDICATOR("Visa Large Ticket Indicator", 1),
        ACCOUNT_LEVEL_PROCESSING_INDICATOR("Account Level Processing Indicator", 1),
        ACCOUNT_FUND_SOURCE("Account Fund Source", 1),
        CARD_CLASS("Card Class", 1),
        PAN_LENGTH_MIN("Primary Account Number (PAN) Length Minimum", 2),
        PAN_LENGTH_MAX("Primary Account Number (PAN) Length Maximum", 2),
        TOKEN_INDICATOR("Token Indicator", 1),
        ISSUING_NETWORK("Issuing Network", 2),
        CARDHOLDER_BILLING_CURRENCY("Cardholder Billing Currency", 3),
        ACCOUNT_FUND_SOURCE_SUB_TYPE("Account Fund Source Sub-Type", 1),
        MONEY_SEND_INDICATOR("Money Send Indicator", 1),
        ORIGINAL_CREDIT_MONEY_TRANSFER_INDICATOR("Original Credit Money Transfer (MT) Indicator", 1),
        ORIGINAL_CREDIT_ONLINE_GAMBLING_INDICATOR("Original Credit Online Gambling (OG) Indicator", 1),
        FAST_FUNDS("Fast Funds", 1),
        ORIGINAL_CREDIT_TRANSACTION_INDICATOR("Original Credit Transaction (OCT) Indicator", 1);

        private final String description;
        private final int length;

        BinFileDetailFieldInfo(final String description, final int length) {
            this.description = "Global BIN File Detail: " + description;
            this.length = length;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public int getLength() {
            return length;
        }
    }

    public BinDetailRecord() {
        super();
    }

    public BinDetailRecord(final String record) throws ParseException {
        super(record);
        parse();
    }

    @Override
    protected RecordTypeIndicator getDefaultRecordTypeIndicator() {
        return RECORD_TYPE_INDICATOR;
    }

    @Override
    protected void parseExtended() throws ParseException {
        setLowBin(getNextField(BinFileDetailFieldInfo.LOW_BIN));
        setHighBin(getNextField(BinFileDetailFieldInfo.HIGH_BIN));
        setBinLength(Integer.parseInt(getNextField(BinFileDetailFieldInfo.BIN_LENGTH)));
        setBinDetailPan(Integer.parseInt(getNextField(BinFileDetailFieldInfo.BIN_DETAIL_PAN)));
        setIssuerBankName(getNextField(BinFileDetailFieldInfo.ISSUER_BANK_NAME));
        setCountryCode(getNextField(BinFileDetailFieldInfo.COUNTRY_CODE));
        setDetailCardProduct(getNextField(BinFileDetailFieldInfo.DETAIL_CARD_PRODUCT));
        setDetailCardIndicator(getNextField(BinFileDetailFieldInfo.DETAIL_CARD_INDICATOR));
        getNextField(FILLER_LENGTH_2); // Filler
        setIssuerUpdateYear(getNextField(BinFileDetailFieldInfo.ISSUER_UPDATE_YEAR));
        setIssuerUpdateMonth(getNextField(BinFileDetailFieldInfo.ISSUER_UPDATE_MONTH));
        setIssuerUpdateDay(getNextField(BinFileDetailFieldInfo.ISSUER_UPDATE_DAY));
        setDebitNetworkPinlessIndicator(getNextField(BinFileDetailFieldInfo.DEBIT_NETWORK_PINLESS_INDICATOR));
        setEbtState(getNextField(BinFileDetailFieldInfo.EBT_STATE));
        setDebitSignatureNetworkParticipant(getNextField(BinFileDetailFieldInfo.DEBIT_SIGNATURE_NETWORK_PARTICIPANT));
        setFsaIndicator(getNextField(BinFileDetailFieldInfo.FSA_INDICATOR));
        getNextField(FILLER_LENGTH_6); // Filler
        getNextField(FILLER_LENGTH_6); // Filler
        getNextField(FILLER_LENGTH_6); // Filler
        setPrepaidIndicator(getNextField(BinFileDetailFieldInfo.PREPAID_INDICATOR));
        setProductId(getNextField(BinFileDetailFieldInfo.PRODUCT_ID));
        setRegulatorIndicator(getNextField(BinFileDetailFieldInfo.REGULATOR_INDICATOR));
        setVisaProductSubtype(getNextField(BinFileDetailFieldInfo.VISA_PRODUCT_SUBTYPE));
        setVisaLargeTicketIndicator(getNextField(BinFileDetailFieldInfo.VISA_LARGE_TICKET_INDICATOR));
        setAccountLevelProcessingIndicator(getNextField(BinFileDetailFieldInfo.ACCOUNT_LEVEL_PROCESSING_INDICATOR));
        setAccountFundSource(getNextField(BinFileDetailFieldInfo.ACCOUNT_FUND_SOURCE));
        setCardClass(getNextField(BinFileDetailFieldInfo.CARD_CLASS));
        setPanLengthMin(Integer.parseInt(getNextField(BinFileDetailFieldInfo.PAN_LENGTH_MIN)));
        setPanLengthMax(Integer.parseInt(getNextField(BinFileDetailFieldInfo.PAN_LENGTH_MAX)));
        setTokenIndicator(YesNoAnswer.fromString(getNextField(BinFileDetailFieldInfo.TOKEN_INDICATOR)));
        setIssuingNetwork(getNextField(BinFileDetailFieldInfo.ISSUING_NETWORK));
        setCardholderBillingCurrency(getNextField(BinFileDetailFieldInfo.CARDHOLDER_BILLING_CURRENCY));
        getNextField(FILLER_LENGTH_4); // Filler
        setAccountFundSourceSubType(getNextField(BinFileDetailFieldInfo.ACCOUNT_FUND_SOURCE_SUB_TYPE));
        getNextField(FILLER_LENGTH_18); // Filler
        setMoneySendIndicator(getNextField(BinFileDetailFieldInfo.MONEY_SEND_INDICATOR));
        setOriginalCreditMoneyTransferIndicator(getNextField(BinFileDetailFieldInfo.ORIGINAL_CREDIT_MONEY_TRANSFER_INDICATOR));
        setOriginalCreditOnlineGamblingIndicator(getNextField(BinFileDetailFieldInfo.ORIGINAL_CREDIT_ONLINE_GAMBLING_INDICATOR));
        setFastFunds(getNextField(BinFileDetailFieldInfo.FAST_FUNDS));
        getNextField(FILLER_LENGTH_5); // Filler
        setOriginalCreditTransactionIndicator(getNextField(BinFileDetailFieldInfo.ORIGINAL_CREDIT_TRANSACTION_INDICATOR));
        getNextField(FILLER_LENGTH_2); // Reserved Field
    }

    public String getLowBin() {
        return lowBin;
    }

    public void setLowBin(final String lowBin) {
        this.lowBin = FormatUtil.optionalField(lowBin, BinFileDetailFieldInfo.LOW_BIN);
    }

    public String getHighBin() {
        return highBin;
    }

    public void setHighBin(final String highBin) {
        this.highBin = FormatUtil.optionalField(highBin, BinFileDetailFieldInfo.HIGH_BIN);
    }

    public int getBinLength() {
        return binLength;
    }

    public void setBinLength(final int binLength) {
        this.binLength = FormatUtil.optionalField(binLength, BinFileDetailFieldInfo.BIN_LENGTH);
    }

    public int getBinDetailPan() {
        return binDetailPan;
    }

    public void setBinDetailPan(final int binDetailPan) {
        this.binDetailPan = FormatUtil.optionalField(binDetailPan, BinFileDetailFieldInfo.BIN_DETAIL_PAN);
    }

    public String getIssuerBankName() {
        return issuerBankName;
    }

    public void setIssuerBankName(final String issuerBankName) {
        this.issuerBankName = FormatUtil.optionalField(issuerBankName, BinFileDetailFieldInfo.ISSUER_BANK_NAME);
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String sCountryCode) {
        this.countryCode = CountryCodeUtil.getCountryCode(sCountryCode);
    }

    public DetailCardProduct getDetailCardProduct() {
        return detailCardProduct;
    }

    public void setDetailCardProduct(final String detailCardProduct) {
        this.detailCardProduct = DetailCardProduct.fromString(detailCardProduct);
    }

    public DetailCardIndicator getDetailCardIndicator() {
        return detailCardIndicator;
    }

    public void setDetailCardIndicator(final String detailCardIndicator) {
        try {
            this.detailCardIndicator = DetailCardIndicator.valueOf(detailCardIndicator);
        } catch (final Exception ex) {
            this.detailCardIndicator = DetailCardIndicator.DEFAULT;
        }
    }

    public String getIssuerUpdateYear() {
        return issuerUpdateYear;
    }

    public void setIssuerUpdateYear(final String issuerUpdateYear) {
        this.issuerUpdateYear = FormatUtil.optionalField(issuerUpdateYear, BinFileDetailFieldInfo.ISSUER_UPDATE_YEAR);
    }

    public String getIssuerUpdateMonth() {
        return issuerUpdateMonth;
    }

    public void setIssuerUpdateMonth(final String issuerUpdateMonth) {
        this.issuerUpdateMonth = FormatUtil.optionalField(issuerUpdateMonth, BinFileDetailFieldInfo.ISSUER_UPDATE_MONTH);
    }

    public String getIssuerUpdateDay() {
        return issuerUpdateDay;
    }

    public void setIssuerUpdateDay(final String issuerUpdateDay) {
        this.issuerUpdateDay = FormatUtil.optionalField(issuerUpdateDay, BinFileDetailFieldInfo.ISSUER_UPDATE_DAY);
    }

    public String getDebitNetworkPinlessIndicator() {
        return debitNetworkPinlessIndicator;
    }

    public void setDebitNetworkPinlessIndicator(final String debitNetworkPinlessIndicator) {
        this.debitNetworkPinlessIndicator =
                FormatUtil.optionalField(debitNetworkPinlessIndicator, BinFileDetailFieldInfo.DEBIT_NETWORK_PINLESS_INDICATOR);
    }

    public String getEbtState() {
        return ebtState;
    }

    public void setEbtState(final String ebtState) {
        this.ebtState = FormatUtil.optionalField(ebtState, BinFileDetailFieldInfo.EBT_STATE);
    }

    public String getDebitSignatureNetworkParticipant() {
        return debitSignatureNetworkParticipant;
    }

    public void setDebitSignatureNetworkParticipant(final String debitSignatureNetworkParticipant) {
        this.debitSignatureNetworkParticipant =
                FormatUtil.optionalField(debitSignatureNetworkParticipant, BinFileDetailFieldInfo.DEBIT_SIGNATURE_NETWORK_PARTICIPANT);
    }

    public FsaIndicator getFsaIndicator() {
        return fsaIndicator;
    }

    public void setFsaIndicator(final String fsaIndicator) {
        setFsaIndicator(FsaIndicator.fromString(fsaIndicator));
    }

    public void setFsaIndicator(final FsaIndicator fsaIndicator) {
        this.fsaIndicator = fsaIndicator;
    }

    public PrepaidIndicator getPrepaidIndicator() {
        return prepaidIndicator;
    }

    public void setPrepaidIndicator(final String prepaidIndicator) {
        setPrepaidIndicator(PrepaidIndicator.fromString(prepaidIndicator));
    }

    public void setPrepaidIndicator(final PrepaidIndicator prepaidIndicator) {
        this.prepaidIndicator = prepaidIndicator;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(final String productId) {
        this.productId = FormatUtil.optionalField(productId, BinFileDetailFieldInfo.PRODUCT_ID);
    }

    public RegulatorIndicator getRegulatorIndicator() {
        return regulatorIndicator;
    }

    public void setRegulatorIndicator(final String regulatorIndicator) {
        setRegulatorIndicator(RegulatorIndicator.fromString(regulatorIndicator));
    }

    public void setRegulatorIndicator(final RegulatorIndicator regulatorIndicator) {
        this.regulatorIndicator = regulatorIndicator;
    }

    public VisaProductSubType getVisaProductSubtype() {
        return visaProductSubtype;
    }

    public void setVisaProductSubtype(final String visaProductSubtype) {
        setVisaProductSubtype(VisaProductSubType.fromString(visaProductSubtype));
    }

    public void setVisaProductSubtype(final VisaProductSubType visaProductSubtype) {
        this.visaProductSubtype = visaProductSubtype;
    }

    public VisaLargeTicketIndicator getVisaLargeTicketIndicator() {
        return visaLargeTicketIndicator;
    }

    public void setVisaLargeTicketIndicator(final String visaLargeTicketIndicator) {
        setVisaLargeTicketIndicator(VisaLargeTicketIndicator.fromString(visaLargeTicketIndicator));
    }

    public void setVisaLargeTicketIndicator(final VisaLargeTicketIndicator visaLargeTicketIndicator) {
        this.visaLargeTicketIndicator = visaLargeTicketIndicator;
    }

    public YesNoAnswer getAccountLevelProcessingIndicator() {
        return accountLevelProcessingIndicator;
    }

    public void setAccountLevelProcessingIndicator(final String accountLevelProcessingIndicator) {
        setAccountLevelProcessingIndicator(YesNoAnswer.fromString(accountLevelProcessingIndicator));
    }

    public void setAccountLevelProcessingIndicator(final YesNoAnswer accountLevelProcessingIndicator) {
        this.accountLevelProcessingIndicator = accountLevelProcessingIndicator;
    }

    public AccountFundSource getAccountFundSource() {
        return accountFundSource;
    }

    public void setAccountFundSource(final String accountFundSource) {
        setAccountFundSource(AccountFundSource.fromString(accountFundSource));
    }

    public void setAccountFundSource(final AccountFundSource accountFundSource) {
        this.accountFundSource = accountFundSource;
    }

    public CardClass getCardClass() {
        return cardClass;
    }

    public void setCardClass(final String cardClass) {
        setCardClass(CardClass.fromString(cardClass));
    }

    public void setCardClass(final CardClass cardClass) {
        this.cardClass = cardClass;
    }

    public int getPanLengthMin() {
        return panLengthMin;
    }

    public void setPanLengthMin(final int panLengthMin) {
        this.panLengthMin = FormatUtil.optionalField(panLengthMin, BinFileDetailFieldInfo.PAN_LENGTH_MIN);
    }

    public int getPanLengthMax() {
        return panLengthMax;
    }

    public void setPanLengthMax(final int panLengthMax) {
        this.panLengthMax = FormatUtil.optionalField(panLengthMax, BinFileDetailFieldInfo.PAN_LENGTH_MAX);
    }

    public YesNoAnswer getTokenIndicator() {
        return tokenIndicator;
    }

    public void setTokenIndicator(final YesNoAnswer tokenIndicator) {
        this.tokenIndicator = tokenIndicator;
    }

    public IssuingNetwork getIssuingNetwork() {
        return issuingNetwork;
    }

    public void setIssuingNetwork(final String issuingNetwork) {
        setIssuingNetwork(IssuingNetwork.fromString(issuingNetwork));
    }

    public void setIssuingNetwork(final IssuingNetwork issuingNetwork) {
        this.issuingNetwork = issuingNetwork;
    }

    public CurrencyCode getCardholderBillingCurrency() {
        return cardholderBillingCurrency;
    }

    public void setCardholderBillingCurrency(final String cardholderBillingCurrency) {
        this.cardholderBillingCurrency = CurrencyCode.getByCodeIgnoreCase(
                FormatUtil.optionalField(cardholderBillingCurrency, BinFileDetailFieldInfo.CARDHOLDER_BILLING_CURRENCY));
    }

    public AccountFundSourceSubType getAccountFundSourceSubType() {
        return accountFundSourceSubType;
    }

    public void setAccountFundSourceSubType(final String accountFundSourceSubType) {
        setAccountFundSourceSubType(AccountFundSourceSubType.fromString(accountFundSourceSubType));
    }

    public void setAccountFundSourceSubType(final AccountFundSourceSubType accountFundSourceSubType) {
        this.accountFundSourceSubType = accountFundSourceSubType;
    }

    public MoneySendIndicator getMoneySendIndicator() {
        return moneySendIndicator;
    }

    public void setMoneySendIndicator(final String moneySendIndicator) {
        setMoneySendIndicator(MoneySendIndicator.fromString(moneySendIndicator));
    }

    public void setMoneySendIndicator(final MoneySendIndicator moneySendIndicator) {
        this.moneySendIndicator = moneySendIndicator;
    }

    public OriginalCreditIndicator getOriginalCreditMoneyTransferIndicator() {
        return originalCreditMoneyTransferIndicator;
    }

    public void setOriginalCreditMoneyTransferIndicator(final String originalCreditMoneyTransferIndicator) {
        setOriginalCreditMoneyTransferIndicator(
                OriginalCreditIndicator.fromString(
                        originalCreditMoneyTransferIndicator, getCountryCode(), getDetailCardProduct()));
    }

    public void setOriginalCreditMoneyTransferIndicator(final OriginalCreditIndicator originalCreditMoneyTransferIndicator) {
        this.originalCreditMoneyTransferIndicator = originalCreditMoneyTransferIndicator;
    }

    public OriginalCreditIndicator getOriginalCreditOnlineGamblingIndicator() {
        return originalCreditOnlineGamblingIndicator;
    }

    public void setOriginalCreditOnlineGamblingIndicator(final String originalCreditOnlineGamblingIndicator) {
        setOriginalCreditOnlineGamblingIndicator(
                OriginalCreditIndicator.fromString(
                        originalCreditOnlineGamblingIndicator, getCountryCode(), getDetailCardProduct()));
    }

    public void setOriginalCreditOnlineGamblingIndicator(final OriginalCreditIndicator originalCreditOnlineGamblingIndicator) {
        this.originalCreditOnlineGamblingIndicator = originalCreditOnlineGamblingIndicator;
    }

    public FastFunds getFastFunds() {
        return fastFunds;
    }

    public void setFastFunds(final String fastFunds) {
        setFastFunds(FastFunds.fromString(fastFunds));
    }

    public void setFastFunds(final FastFunds fastFunds) {
        this.fastFunds = fastFunds;
    }

    public OriginalCreditIndicator getOriginalCreditTransactionIndicator() {
        return originalCreditTransactionIndicator;
    }

    public void setOriginalCreditTransactionIndicator(final String originalCreditTransactionIndicator) {
        setOriginalCreditTransactionIndicator(
                OriginalCreditIndicator.fromString(
                        originalCreditTransactionIndicator, getCountryCode(), getDetailCardProduct()));
    }

    public void setOriginalCreditTransactionIndicator(final OriginalCreditIndicator originalCreditTransactionIndicator) {
        this.originalCreditTransactionIndicator = originalCreditTransactionIndicator;
    }
}
