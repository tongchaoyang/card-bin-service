package home.tong.card.bin.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class BINBO extends AbstractBO implements Comparable<BINBO> {

    private String bin;
    private String lowBin;
    private String highBin;
    private CardType cardType;
    private CardClass cardClass;
    private Network network;
    private Bank issuerBank;
    private boolean currencyExchangeSupport;
    private boolean octEligible;
    private boolean prepaidIndicator;

    @Override
    public int compareTo(final BINBO o) {
        if (this == o) {
            return 0;
        }
        if (o == null) {
            return 1;
        }
        return lowBin.compareTo(o.lowBin);
    }


    public enum CardType {
        debit,
        credit,
        prepaid
    }

    public enum Network {
        visa,
        mastercard,
        amex,
        discover
    }

    public enum CardClass {
        business,
        corporate,
        purchase,
        consumer
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("IINBO {")
                .append("bin=").append(StringUtils.wrap(bin, '\''))
                .append(", lowBin=").append(StringUtils.wrap(lowBin, '\''))
                .append(", highBin=").append(StringUtils.wrap(highBin, '\''))
                .append(", cardType=").append(cardType)
                .append(", cardClass=").append(cardClass)
                .append(", network=").append(network)
                .append(", issuerBank=").append(issuerBank)
                .append(", currencyExchangeSupport=").append(currencyExchangeSupport)
                .append(", octEligible=").append(octEligible)
                .append(", prepaidIndicator=").append(prepaidIndicator)
                .append("}")
                .toString();
    }

    @Data
    @AllArgsConstructor
    public static class Bank {
        private String name;
        private String country;
        private String currency;
    }
}
