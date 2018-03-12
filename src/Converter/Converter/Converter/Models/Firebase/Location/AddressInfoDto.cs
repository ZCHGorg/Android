using System;

namespace Converter.Models.Firebase
{
    public class AddressInfoDto
    {
        public int ID { get; set; } = 0;
        public String Title { get; set; } = string.Empty;
        public String AddressLine1 { get; set; } = string.Empty;
        public String Town { get; set; } = string.Empty;
        public String StateOrProvince { get; set; } = string.Empty;
        public String Postcode { get; set; } = string.Empty;
        public int CountryID { get; set; } = 0;
        public double Latitude { get; set; } = 0.0;
        public double Longitude { get; set; } = 0.0;
        public int DistanceUnit { get; set; } = 0;
        public String ContactTelephone1 { get; set; } = string.Empty;
        public String RelatedURL { get; set; } = string.Empty;
        public String AddressLine2 { get; set; } = string.Empty;
        public String AccessComments { get; set; } = string.Empty;
        public String ContactEmail { get; set; } = string.Empty;
        public String ContactTelephone2 { get; set; } = string.Empty;
    }
}
