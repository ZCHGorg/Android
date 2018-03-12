using System;
using System.Collections.Generic;

namespace Converter.Models.Firebase
{
    public class ChargeStationDto
    {
        public int ID { get; set; } = 0;
        public String UUID { get; set; } = string.Empty;
        public int DataProviderID { get; set; } = 0;
        public int OperatorID { get; set; } = 0;
        public String OperatorsReference { get; set; } = string.Empty;
        public int UsageTypeID { get; set; } = 0;
        public String UsageCost { get; set; } = string.Empty;
        public AddressInfoDto AddressInfo { get; set; } = new AddressInfoDto();
        public int NumberOfPoints { get; set; } = 0;
        public int StatusTypeID { get; set; } = 0;
        public String DateLastStatusUpdate { get; set; } = string.Empty;
        public int DataQualityLevel { get; set; } = 0;
        public String DateCreated { get; set; } = string.Empty;
        public int SubmissionStatusTypeID { get; set; } = 0;
        public List<ConnectionDto> Connections { get; set; } = new List<ConnectionDto>();
        public Boolean IsRecentlyVerified { get; set; } = false;
        public String DateLastVerified { get; set; } = string.Empty;
        public String GeneralComments { get; set; } = string.Empty;
        public List<MediaItemDto> MediaItems { get; set; } = new List<MediaItemDto>();
        public List<CommentDto> UserComments { get; set; } = new List<CommentDto>();
        public String EthAddress { get; set; } = string.Empty;
        public double CostCharge { get; set; } = 0.0;
        public double CostPark { get; set; } = 0.0;
    }
}
