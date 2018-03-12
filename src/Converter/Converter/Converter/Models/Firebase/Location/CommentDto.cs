using System;

namespace Converter.Models.Firebase
{
    public class CommentDto
    {
        private int ChargePointID { get; set; } = 0;
        private int CheckinStatusTypeID { get; set; } = 0;
        private String Comment { get; set; } = string.Empty;
        private int CommentTypeID { get; set; } = 0;
        private String DateCreated { get; set; } = string.Empty;
        private int ID { get; set; } = 0;
        private String RelatedURL { get; set; } = string.Empty;
        private UserDto User { get; set; } = new UserDto();
        private String UserName { get; set; } = string.Empty;
    }
}