# EconTale
EconTale is a complex economy system for Hytale servers designed with the ability to get and modify player data when offline

## Features
- Offline Player Data Management: Access and modify player economy component data even when they are not online.
- Easy to use API: Simple functions to get, set, and modify player economy data.
- Easy to use events for further control over the economy system.

## Installation
1, Download from the curse forge page (COMING SOON)  
2, Place the mod in your plugin folder  
3, Restart your server then stop it again if you wish to edit the config  
4, Configure the mod using the config and lang file located in the RWS_EconTale folder  
5, Boot up the server and enjoy!  

## Usage
### Server usage
To find all the commands and permissions (if needed) use `/eco`  
Some commands are both a sub command and a base.
The following are also base commands:
- `/eco balance` or more easily `/bal` - Check your balance (or --p <player> for others)  
- `/eco deposit` or `/pay <player> <amount>` - Deposit money into target players account
- `/eco balancetop` or `/baltop` - View the richest players on the server (count and format all configurable)

### API usage
The Mod provides many intuitive methods of getting and modifying player economy data.  
#### Getting Player Data
To get a player's economy data, use the following method:
`CurrencyUtil.getOrAddCurrencyComponent(uuid)`  
and there you have it, you now have the CurrencyComponent (their wallet) of the player with the specified UUID.  
Its really that simple! Despite the Component being stored on the Players EntityStore you can even access it offline with this!

#### Basic Economy Methods
Setting the player's balance is as simple as `CurrencyComponent#setBalance(amount)`
To add money to a player's balance, use `CurrencyComponent#deposit(amount)`
To remove money from a player's balance, use `CurrencyComponent#tryWithdraw(amount)`
and to transfer money between two players use `CurrencyUtil#tryTransferMoney(walletFrom, walletTo, amount)`  
  
There are some more complex methods for more control like, for example:  
`CurrencyComponent#deposit(double amount, MoneyEventReason reason, boolean ignoreCanceled)`

#### Events
Here are the current available events:  
`HycononyMoneyAddEvent.Pre` - Fired before money is added to a player's account,  
you can modify the amount or cancel the event.  
`HycononyMoneyAddEvent.Post` - Fired after money has been added to a player's account.  
`HycononyMoneyRemoveEvent.Pre` - Same concept as the AddEvent.Pre but for removing money.
`HycononyMoneyRemoveEvent.Post` - Same concept as the AddEvent.Post but for removing money.