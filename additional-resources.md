**Data**:

For collecting data on solar, wind, and hydro energy, we recommend utilizing resources such as [Fingrid.fi](https://www.fingrid.fi/), which provides comprehensive data on energy generation and consumption. By accessing this platform, users can retrieve real-time and historical data to monitor renewable energy sources effectively.

**Error handling:**

To ensure data integrity, the system must implement error-handling mechanisms. This includes detecting various types of errors and possibly providing clear guidance to users on how to rectify them. Below are several possible scenarios and how the system should handle them:

- Incorrect date format:
	- The user attempts to enter the date: "April 12, 2024."
		- The system expects the date format "DD/MM/YYYY."
		- The system detects the incorrect date format and displays an error message: "Invalid date format. Please enter the date in the format  
		'DD/MM/YYYY'."
		- Along with the error message, the system displays an example of correct date formatting using the real date: "For example, enter '12/04/2024' for  
		April 12, 2024."
- No Available Data for Selected Date:
	- The user enters the date: "15/04/2024" for April 15, 2024.
		- The system searches for available data but finds none for the selected date.
		- The system displays a message: "No available data for the selected date. Please choose another date."
		- The user is prompted to correct the date input or select another date.

**Using Other API Providers:**

While Fingrid API is a good option, you can explore other API providers for renewable energy data if needed. Ensure seamless integration and compatibility with your system.

**Note**:

This is just to make your implementation easy and to provide you with possible direction on a couple of aspects of the project. Note that this guidance doesn’t limit you from exploring other possibilities or ways of implementation. Also, if you limit yourself to handling only the exemplary error handling scenarios, this will not give you any credits.