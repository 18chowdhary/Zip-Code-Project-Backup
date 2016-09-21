/**
 * Zip Code Project - Shreya Chowdhary
 * This class runs a program that reads in a file of barcodes and converts the barcodes into zipcodes, finding all cities that match with these zipcodes. 
 * The program also reads a file of zipcodes and finds cities that match with the zipcode. 
 * 
 * Started: 09/14/16 
 * Finished: 09/19/16
 */
import java.util.Scanner; 
import java.io.File;
import java.io.FileNotFoundException;

public class zipCode 
{	
	/** 
	 * Calculates the value of a digit of a zipcode. 
	 * Pre: Must have read the barcode. 
	 * @param digit - the five character digit that is being reverse-encoded. 
	 * @return digitValue - the calculated value of the digit 
	 */
	public static int calculateDigitValue(String digit) 
	{
		//create an array to hold each character of the digit
		int[] digitParts = new int[5]; 
		
		//fill the array created above by parsing through the digit and converting each part to its respective encoding
		for (int j = 0; j < digit.length(); j++) 
		{ 
			//current character of digit under consideration
			char digPart = digit.charAt(j); 
			
			//reverse encoding - if full bar, 1; otherwise, 0 
			if(digPart == '|') 
			{ 
				digitParts[j] = 1;
			}
			else 
			{
				digitParts[j] = 0; 
			}
		}
		
		//calculating the digit value using the given formula
		int digitValue = 7*digitParts[0]+4*digitParts[1]+2*digitParts[2]+digitParts[3];
		
		//Check for 11
		if (digitValue == 11)
		{
			digitValue = 0; 
		}
		
		return digitValue; 
	}
	
	/**
	 * Creates a zipcode from a given barcode.
	 * Pre: must have created scanner to read the file
	 * @param barcode - the barcode read from the file
	 * @return zipCode or error message indicating the barcode was not valid
	 */
	public static String createZipCode(String barcode) 
	{
		//removing beginning and end frame bars
		String shortBarcode = barcode.substring(1, barcode.length()-1); 
		String zipCode = ""; 
		
		//Calculating the value of each digit and adding it to the zipcode
		for (int i = 0; i < shortBarcode.length()-5; i+=5)
		{ 
			String digit = shortBarcode.substring(i, i+5);
			int digitValue = calculateDigitValue(digit); 
			
			zipCode += digitValue; 
		}
		
		//calculating the check digit and checking if zipcode is invalid 
		int checkDigitValue = calculateDigitValue(shortBarcode.substring(shortBarcode.length()-5)); 
		
		//calculating the sum of the digits of the zipcode
		int zipSum = 0; 
		for (int i = 0; i < zipCode.length(); i++)
		{
			//converting the current character of the zipcode into an integer 
			char dig = zipCode.charAt(i); 
			String digit = Character.toString(dig); 
			int digitValue = Integer.parseInt(digit); 
			
			//adding it to the sum of the digits 
			zipSum+=digitValue; 
		} 
		
		//checking if sum of all digits, including check digits is a multiple of 10
		int checkSum = zipSum+checkDigitValue; 
		if (checkSum%10 == 0) //valid zipcode, returns the zipcode
		{ 
			return zipCode;
		}
		else //invalid, returns an error message
		{
			return "ERROR - invalid check digit!";
		}
		
	}
	
	/**
	 * Creates a barcode from a given zipcode.
	 * Pre: must have obtained zipcode from file (initalized scanner to read file).
	 * @param zipCode
	 * @return barcode - the string barcode
	 */
	public static String[] createBarcode(String zipCode) 
	{
		String[] barcode = new String[6];
		
		int digSum = 0; //keeping track of the sum of the digits
		
		//Creating zipcode - fill the characters inside the frames 
		//parse through zipcode to convert each digit into its corresponding encoding
		for (int i = 0; i < zipCode.length(); i++)
		{
			//convert the digit into an integer 
			char digit = zipCode.charAt(i); 
			String dig = Character.toString(digit); 
			int digNum = Integer.parseInt(dig); 				
			//add to sum
			digSum += digNum; 
				
			//get correcting encoding for digit
			barcode[i] = getBarcodeEncoding(digNum); 
		}
		
		//get check digit
		barcode[5] = getBarcodeEncoding(getCheckDigit(digSum)); 
		
		return barcode; 
	}
	
	/**
	 * Gets the correct encoding for a digit.
	 * @param num - the digit
	 * @return String encoding value - according to the chart
	 */
	public static String getBarcodeEncoding(int num)
	{
		switch (num) 
		{
			case 1: return ":::||"; 
			case 2: return "::|:|";
			case 3: return "::||:"; 
			case 4: return ":|::|"; 
			case 5: return ":|:|:"; 
			case 6: return ":||::"; 
			case 7: return "|:::|"; 
			case 8: return "|::|:"; 
			case 9: return "|:|::"; 
			case 0: 
			case 10: return "||:::"; 
			default: return "Invalid - error finding digit of zipcode"; 
		}
	}
	
	/**
	 * Calculates the check digit.
	 * Pre: must have read through barcode and calculated the sum of digits in the zipcode.
	 * @param digSum - the sum of digits in the zipcode
	 * @return checkDigit - integer value calculated based on given formula
	 */
	public static int getCheckDigit(int digSum) 
	{ 
		int checkDigit = 10 - digSum%10; 
		return checkDigit; 
	}
	
	/**
	 * Gets all the cities with the given zipcode. 
	 * @param zipCode
	 * @param cities
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String[] getCities(String zipCode, File cities) throws FileNotFoundException 
	{
		//Checking that the zipcode was valid
		if (zipCode.indexOf("ERROR") != -1) 
		{ 
			String[] error = {"No Location Found"};
			return error; 
		}
		else
		{
			//Calculating the number of cities with the same zipcode
			Scanner cityReader = new Scanner(cities); 
			int numCities = 0;  
			while (cityReader.hasNextLine())
			{ 
				String currCity = cityReader.nextLine();
				String[] currCityData = currCity.split(","); 
					
				if (currCityData[0].equals(zipCode))
				{	
					numCities++; 
				}
			}
			
			//creating an array to hold all the cities with the same zipcode
			String[] sameZipCities = new String[numCities]; 
			
			//filling the above array
			Scanner cityChecker = new Scanner(cities); 
			int index = 0; 
			while (index < numCities) 
			{	
				while (cityChecker.hasNextLine())
				{ 
					String currCity = cityChecker.nextLine(); 
					String[] currCityData = currCity.split(","); 
						
					if (currCityData[0].equals(zipCode)) 
					{
						sameZipCities[index] = currCityData[0]+"\t"+currCityData[1]+"\t"+currCityData[2]; 
						index++; 
					}
				}
			}
			
			return sameZipCities; 
		}
	}
	
	/**
	 * Prints the correct type of barcode. 
	 * Pre: must have created the barcode.
	 * Post: prints barcodes.
	 * @param barcode - the barcode
	 * @param readable - whether it is readable or not 
	 */
	public static void printBarcode(String[] barcode, boolean readable)
	{
		String printedBarcode = ""; 
		if (readable == true)
		{	
			printedBarcode = "|\t"; 
			for (int i = 0; i < barcode.length; i++) 
			{
				printedBarcode += barcode[i]+"\t"; 
			}
		}
		else //postable
		{
			printedBarcode = "|"; 
			for (int i = 0; i < barcode.length; i++)
			{
				printedBarcode += barcode[i]; 
			}
		}
		
		printedBarcode += "|"; 
		System.out.print(printedBarcode);
		
	}
	
	/**
	 * Prints out barcodes.
	 * Pre: must have zipcode.
	 * Post: prints out barcodes.
	 * @param zipCode - the zipcode
	 */
	public static void printBarcodes(String zipCode)
	{
		//print zipcode & barcodes 
		System.out.println(zipCode);
		System.out.print("\tReadable Barcode ");
		printBarcode(createBarcode(zipCode), true);
		System.out.println();
		System.out.print("\tPostable Barcode ");
		printBarcode(createBarcode(zipCode), false); 
		System.out.println(); 
	}
	
	/**
	 * Prints all cities with the same zip code as the current zip code.
	 * Pre: must have found the cities.
	 * Post: prints out all the cities.
	 * @param cities - array that contains all the cities. 
	 */
	public static void printCities(String[] cities) 
	{
		for (int k = 0; k < cities.length; k++) 
		{ 
			System.out.println(cities[k]);
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException 
	{
		//initiliazing files & scanners 
		File zipCodes = new File("src/ZipCodes.txt"); 
		File cities = new File("src/ZipCodesCity.txt"); 
		File barCodes = new File("src/ZipBarCodes.txt"); 
		Scanner reader = new Scanner(zipCodes); 
		Scanner barcodeReader = new Scanner(barCodes); 
		String zipCode = "";
		
		//Option 1 & 2
		System.out.println("OPTION 1 & 2");
		//reading whole file
		while (reader.hasNext())
		{	
			//obtain zipcode
			zipCode = reader.next();  
			
			//get all cities with same zip code
			String[] citiesWithSameZip = getCities(zipCode, cities); 
			
			//print cities & barcode
			printCities(citiesWithSameZip); 
			printBarcodes(zipCode);
			
			System.out.println(); 
		}
		
		//Option 3
		System.out.println("OPTION 3");
		//reading the whole file
		while (barcodeReader.hasNext()) 
		{ 
			//get barcode
			String barcode = barcodeReader.next(); 
			
			//create zipcode and print out zipcode and bar code
			String zip = createZipCode(barcode); 
			System.out.println(barcode+" ---> "+zip); 
			
			//get cities with the same zipcode and print out all cities
			String[] citiesWithSameZip = getCities(zip, cities);
			printCities(citiesWithSameZip);
			
			System.out.println(); 
		}
	}

}
