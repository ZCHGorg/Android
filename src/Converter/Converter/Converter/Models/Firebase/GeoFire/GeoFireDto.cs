using Newtonsoft.Json;

public class GeoFireDto
{
    [JsonProperty(PropertyName = "g")]
    public string Name { get; set; }

    [JsonProperty(PropertyName = "l")]
    public double[] Location { get; set; }
}