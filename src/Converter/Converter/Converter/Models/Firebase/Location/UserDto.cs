using System;

namespace Converter.Models.Firebase
{
    public class UserDto
    {
        public int ID { get; set; } = 0;
        public String ProfileImageURL { get; set; } = string.Empty;
        public int ReputationPoints { get; set; } = 0;
        public String Username { get; set; } = string.Empty;
    }
}
