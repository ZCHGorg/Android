package io.charg.chargstation.root;

import java.math.BigInteger;

/**
 * Created by worker on 02.11.2017.
 */

public class CommonData {

    public static final String GEOFIRE_PATH = "geofire";
    public static final String LOCATIONS_PATH = "locations";
    public static final String FIREBASE_PATH_NODES = "nodes";

    public static final double RADIUS_LIMIT = 75;
    public static final String TAG = "charge";

    //main net
    /*public static final String ETH_URL = "https://mainnet.infura.io/Zp6evGImttk7WOe95WcW";
    public static final String SMART_CONTRACT_ADDRESS = "0xC4A86561cb0b7EA1214904f26E6D50FD357C7986";*/

    //rinkeby
    /*public static final String ETH_URL = "https://rinkeby.infura.io/Zp6evGImttk7WOe95WcW";
    public static final String SMART_CONTRACT_ADDRESS = "0x9E27eBE20976782100caA8d6AE60821162C4D3Cf";*/

    //using
    public static final String ETH_URL = "https://mainnet.infura.io/Zp6evGImttk7WOe95WcW";
    public static final String SMART_CONTRACT_ADDRESS = "0xC4A86561cb0b7EA1214904f26E6D50FD357C7986";

    public static final BigInteger GAS_LIMIT_BIG = BigInteger.valueOf(700000);
}
