using System;

namespace Converter.Models.Firebase
{
    public class MediaItemDto
    {
        public int ChargePointID { get; set; } = 0;
        public String Comment { get; set; } = string.Empty;
        public String DateCreated { get; set; } = string.Empty;
        public int ID { get; set; } = 0;
        public bool IsEnabled { get; set; } = false;
        public bool IsExternalResource { get; set; } = false;
        public bool IsFeaturedItem { get; set; } = false;
        public bool IsVideo { get; set; } = false;
        public String ItemThumbnailURL { get; set; } = string.Empty;
        public String ItemURL { get; set; } = string.Empty;
        public UserDto User { get; set; } = new UserDto();
    }
}
