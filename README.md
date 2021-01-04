• ████████╗░█████╗░██╗░░░██╗██████╗░██╗░██████╗████████╗  ░█████╗░██████╗░██████╗░
• ╚══██╔══╝██╔══██╗██║░░░██║██╔══██╗██║██╔════╝╚══██╔══╝  ██╔══██╗██╔══██╗██╔══██╗
• ░░░██║░░░██║░░██║██║░░░██║██████╔╝██║╚█████╗░░░░██║░░░  ███████║██████╔╝██████╔╝
• ░░░██║░░░██║░░██║██║░░░██║██╔══██╗██║░╚═══██╗░░░██║░░░  ██╔══██║██╔═══╝░██╔═══╝░
• ░░░██║░░░╚█████╔╝╚██████╔╝██║░░██║██║██████╔╝░░░██║░░░  ██║░░██║██║░░░░░██║░░░░░
• ░░░╚═╝░░░░╚════╝░░╚═════╝░╚═╝░░╚═╝╚═╝╚═════╝░░░░╚═╝░░░  ╚═╝░░╚═╝╚═╝░░░░░╚═╝░░░░░

@Author Aladdine BEN ROMDHANE aladdineben@outlook.com
@Author Quitterie PILON       quitterie.p@outlook.fr
@Author Dimitri ROMANO        dimitri.romano@etu.cyu.fr

This application has been made in the context of a project in Developping Application
class for the CY Cergy Paris Université. We would like to thank Manos Katsomallos for
teaching us everything to make this application.

• Description of the application: 
    "Tourist app" is a Pokemon Go like application where you need to explore a set of
    defined places across France (such as monuments, museums, stadiums, churches, castles).
    As much as you explore thoses places you will earn points to try reach the maximum and be
    the Tourist app's Indiana Jones.
    Q : How to validate a place? 
    A : To validate a place you need to be at a maximum distance of 200 meters for the place
        you want to validate (Geolocation).

• Activities : MainActivity, RegisterActivity, DashboardActivity [w/ two fragments UserFragment and MapsFragment]

    MainActivity      -> This activity allows the user to try and connect after his registration in the RegisterActivity.
                         He will need to put his login informations (Pseudo, password)
                         Two buttons will be shown to him => ♦ Login (after entering his login informations [The next
                                                               activity will be DashboardActivity])
                                                             ♦ Register (if he doesn't have an account yet [The next
                                                               activity will be RegisterActivity])
    RegisterActivity -> This activity allows the user to register himself by entering his account's informations such as
                        his pseudo, password, email, age, profile image [after he accepted the permission to access his
                        device's storage].
                        Two buttons will be show to him => ♦ Upload (will allow him to select an image from the device's
                                                             storage [only if he accepted the permission, if he didn't the
                                                             button will not be clickable].
                                                           ♦ Register (after entering his account information, a new account
                                                             will be created [added to database] and the next activity will
                                                             be DashboardActivity).
     
                                                             
                                                            
