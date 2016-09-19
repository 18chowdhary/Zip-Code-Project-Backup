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
		String[] barcode = new String[8];
		
		//start and end frames
		barcode[0] = "|";
		barcode[7] = "|"; 
		
		int digSum = 0; //keeping track of the sum of the digits
		
		//Creating zipcode - fill the characters inside the frames 
		int barcodeIndex = 1; 
		for (int j = 1; j < 6; j++)
		{
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
				barcode[j] = getBarcodeEncoding(digNum); 
			}
		}
		
		//get check digit
		barcode[6] = getBarcodeEncoding(getCheckDigit(digSum)); 
		
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
		if (zipCode.indexOf("ERROR") != -1) 
		{ 
			String[] error = {"No Location Found"};
			return error; 
		}
		else
		{
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
				
			String[] sameZipCities = new String[numCities]; 			
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
	
	public static void printBarcode(String[] barcode, boolean readable)
	{
		if (readable == true)
		{
			for (int i = 0; i < barcode.length; i++) 
			{
				System.out.print(barcode[i]+"  ");
			}
		}
		else 
		{
			for (int i = 0; i < barcode.length; i++)
			{
				System.out.print(barcode[i]);
			}
		}
		
	}
	
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
	
	public static void printCities(String[] cities) 
	{
		for (int k = 0; k < cities.length; k++) 
		{ 
			System.out.println(cities[k]);
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException 
	{
		File zipCodes = new File("src/ZipCodes.txt"); 
		File cities = new File("src/ZipCodesCity.txt"); 
		File barCodes = new File("src/ZipBarCodes.txt"); 
		Scanner reader = new Scanner(zipCodes); 
		String zipCode = "";
		
		System.out.println("OPTION 1 & 2");
		while (reader.hasNext())
		{	
			//obtain city data 
			zipCode = reader.next();  
			
			String[] citiesWithSameZip = getCities(zipCode, cities); 
			
			printCities(citiesWithSameZip); 
			printBarcodes(zipCode);
			
			System.out.println(); 
		}
		
		System.out.println("OPTION 3");
		Scanner barcodeReader = new Scanner(barCodes); 
		while (barcodeReader.hasNext()) 
		{ 
			String barcode = barcodeReader.next(); 
			String zip = createZipCode(barcode); 
			System.out.println(barcode+" ---> "+zip); 
			
			String[] citiesWithSameZip = getCities(zip, cities);
			printCities(citiesWithSameZip);
			System.out.println(); 
		}
	}

}