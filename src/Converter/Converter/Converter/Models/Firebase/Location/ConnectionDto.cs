namespace Converter.Models.Firebase
{
    public class ConnectionDto
    {
        public int ID { get; set; } = 0;
        public int ConnectionTypeID { get; set; } = 0;
        public int StatusTypeID { get; set; } = 0;
        public int LevelID { get; set; } = 0;
        public int Amps { get; set; } = 0;
        public int Voltage { get; set; } = 0;
        public double PowerKW { get; set; } = 0.0;
        public int CurrentTypeID { get; set; } = 0;
        public int Quantity { get; set; } = 0;
    }
}
