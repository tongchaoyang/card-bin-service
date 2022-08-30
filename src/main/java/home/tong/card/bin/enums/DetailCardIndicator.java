package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Detail Card Indicator
 */
public enum DetailCardIndicator {
    C(DebitCreditIndicator.CREDIT, PinSignatureCapability.BOTH, "Credit Hybrid"),
    E(DebitCreditIndicator.DEBIT, PinSignatureCapability.PIN, "Debit - PIN Only with EBT"),
    H(DebitCreditIndicator.DEBIT, PinSignatureCapability.BOTH, "Debit Hybrid - PIN and Signature"),
    J(DebitCreditIndicator.DEBIT, PinSignatureCapability.SIGNATURE, "USA Commercial Debit - Signature Only"),
    K(DebitCreditIndicator.DEBIT, PinSignatureCapability.BOTH, "USA Commercial Debit - PIN Capable"),
    L(DebitCreditIndicator.DEBIT, PinSignatureCapability.SIGNATURE, "NON USA Consumer Debit - No PIN Access"),
    M(DebitCreditIndicator.DEBIT, PinSignatureCapability.SIGNATURE, "NON USA Commercial Debit - No PIN Access"),
    N(DebitCreditIndicator.DEBIT, PinSignatureCapability.BOTH, "NON USA Consumer Debit - PIN Capable"),
    O(DebitCreditIndicator.DEBIT, PinSignatureCapability.BOTH, "NON USA Commercial Debit - PIN Capable"),
    P(DebitCreditIndicator.DEBIT, PinSignatureCapability.PIN, "Debit - PIN Only without EBT"),
    R(DebitCreditIndicator.CREDIT, PinSignatureCapability.SIGNATURE, "Private Label (MasterCard)"),
    S(DebitCreditIndicator.DEBIT, PinSignatureCapability.SIGNATURE, "Debit - Signature Only (Not PIN Capable)"),
    X(DebitCreditIndicator.CREDIT, PinSignatureCapability.SIGNATURE, "True credit (No PIN/Signature Capable)"),
    DEFAULT(DebitCreditIndicator.DEFAULT, PinSignatureCapability.NONE, StringUtils.EMPTY);

    private DebitCreditIndicator issueType;
    private PinSignatureCapability capability;
    private String description;

    DetailCardIndicator(final DebitCreditIndicator issueType, final PinSignatureCapability capability, final String description) {
        this.issueType = issueType;
        this.capability = capability;
        this.description = description;
    }

    public DebitCreditIndicator getIssueType() {
        return issueType;
    }

    public PinSignatureCapability getCapability() {
        return capability;
    }

    public String getDescription() {
        return description;
    }
}
