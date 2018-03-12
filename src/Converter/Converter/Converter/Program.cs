using Converter.Models.Firebase;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;

namespace Converter
{
    class Program
    {
        static void Main(string[] args)
        {
            var json = File.ReadAllText("input.json");
            var inputModels = JsonConvert.DeserializeObject<InputModel[]>(json);
            Console.WriteLine($"Entries count: {inputModels.Length}");
            Console.WriteLine();

            var geoFireDict = new Dictionary<string, GeoFireDto>();
            foreach (var item in inputModels)
            {
                var model = new GeoFireDto
                {
                    Name = SideGeoFire.Geohash.Encode(item.AddressInfo.Latitude, item.AddressInfo.Longitude),
                    Location = new double[] { item.AddressInfo.Latitude, item.AddressInfo.Longitude }
                };
                geoFireDict[item.ID] = model;
            }

            Console.WriteLine($"Saving geofire database...");
            File.WriteAllText($"geofire_{DateTime.Now.ToString("dd-MM-yyyy_hh-mm")}.json", JsonConvert.SerializeObject(geoFireDict));
            Console.WriteLine($"Geofire saved");
            Console.WriteLine();

            var locationsInput = JsonConvert.DeserializeObject<List<ChargeStationDto>>(json);
            var locationsDict = new Dictionary<string, ChargeStationDto>();

            //demo data
            //foreach (var item in locationsInput)
            //{
            //    item.EthAddress =  "0x1aa494ff7a493e0ba002e2d38650d4d21bd5591b";
            //    item.CostCharge = 0.04;
            //    item.CostPark = 0.001;
            //    locationsDict[item.ID.ToString()] = item;
            //}

            foreach (var item in locationsInput)
            {
                item.EthAddress = string.Empty;
                item.CostCharge = 0.0;
                item.CostPark = 0.0;
                locationsDict[item.ID.ToString()] = item;
            }

            Console.WriteLine($"Saving location database...");
            File.WriteAllText($"locations_{DateTime.Now.ToString("dd-MM-yyyy_hh-mm")}.json", JsonConvert.SerializeObject(locationsDict));
            Console.WriteLine($"Location saved");

            Console.WriteLine("done");
            Console.Read();

        }
    }
}
