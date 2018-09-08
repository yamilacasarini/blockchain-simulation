import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeeResponse {

    Integer high_fee_per_kb;

    public Integer getHigh_fee_per_kb() {
        return high_fee_per_kb;
    }
}
