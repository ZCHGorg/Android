public class InputModel
{    
    public string ID { get; set; }

    public AddressInfo AddressInfo { get; set; }
}

public class AddressInfo
{
    public double Latitude { get; set; }

    public double Longitude { get; set; }
}