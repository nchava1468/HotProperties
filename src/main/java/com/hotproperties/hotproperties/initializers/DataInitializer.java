package com.hotproperties.hotproperties.initializers;


import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.PropertyImage;
import com.hotproperties.hotproperties.entity.Role;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.repository.PropertyImageRepository;
import com.hotproperties.hotproperties.repository.PropertyRepository;
import com.hotproperties.hotproperties.repository.RoleRepository;
import com.hotproperties.hotproperties.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PropertyRepository propertyRepository, PropertyImageRepository propertyImageRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.propertyRepository = propertyRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {



        if (roleRepository.count() == 0) {
            Role roleBuyer = new Role("ROLE_BUYER");
            Role roleAdmin = new Role("ROLE_ADMIN");
            Role roleAgent = new Role("ROLE_AGENT");

            roleRepository.save(roleBuyer);
            roleRepository.save(roleAdmin);
            roleRepository.save(roleAgent);

            System.out.println("🟢 Initial roles inserted.");
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "Admin",
                    "User",
                    "admin@hotproperties.com",
                    Set.of(roleAdmin));

            userRepository.save(admin);

            System.out.println("🟢 Admin user created.");
        }

        if (propertyRepository.count() == 0 || propertyImageRepository.count() == 0) {

            Property property1 = new Property(
                    "3818 N Christiana Ave",
                    1025000,
                    "Experience luxury living in this beautifully redesigned single-family home, where expert craftsmanship and meticulous attention to detail shine throughout. The sun-drenched open-concept main level boasts a seamless flow between the living and dining areas, complemented by a cozy family room featuring a gas fireplace and elegant doors leading to the rear patio & spacious back yard. The stunning chef's kitchen is a true showpiece, featuring custom cabinetry, a designer tile backsplash, high-end stainless steel appliances, and an expansive center island-perfect for entertaining. Upstairs, the spacious primary suite offers a spa-like retreat with a luxurious ensuite featuring a frameless glass shower and dual vanities. Step through the patio doors to enjoy a large private balcony, perfect for morning coffee and fresh air. Two additional bedrooms, a stylish full bath, and generous storage complete the second floor, including a bright and airy bedroom that features a beautiful terrace overlooking the tree-lined street-an inviting space to relax and enjoy the neighborhood views. The sunlit third level is the epitome of this home, offering a bright and inviting alternative to a traditional basement. This exceptional space features a fourth bedroom, another full guest bath, a dedicated laundry area, and a versatile, spacious, recreation room complete with a wet bar, wine fridge, and a walk-out terrace. Flooded with natural light, it provides the perfect blend of comfort and functionality for both relaxing and entertaining. Outside, the professionally landscaped sizable yard offers a picturesque setting, while the two-car garage adds convenience. Situated in a sought-after neighborhood, this stunning home boasts incredible curb appeal and is truly a must-see! All of the outdoor spaces, including the charming front porch, balconies off of multiple rooms, rear patio, and beautifully designed yard, are unique highlights that enhance this home's appeal!\n",
                    "Chicago, IL 60618",
                    3600);

            Property property2 = new Property(
                    "3423 N Kedzie Ave",
                    899000,
                    "Oversized all-brick single-family home in East Avondale between Logan Square and Irving Park.  Approximately 4600 sq. ft.  6 large bedrooms and 3.5 baths.  Front entrance foyer and great mudroom in the rear.  High ceilings.  Large eat-in kitchen with tons of cabinets and big walk-in pantry.  The top floor has 3 big bedrooms including an enormous master suite with his and her walk-in closets, a large laundry room, and skylights.  The lower level has good natural light with 9-foot ceilings and a true in-law/2nd unit suite with another 3 huge bedrooms/bath/living room/full kitchen and a 2nd laundry room!  2 zones of heating and cooling.  Interior/exterior drain tiles.  Professionally landscaped and gated front and back yards with wonderful perennial plants.  2.5 car garage with room for storage.  Terrific roof deck with trex decking and pergola!  Great location only 3 blocks to the Belmont Blue Line stop.  Easy access to 90/94.  Near great public and private schools, 312 RiverRun trail system, parks, taprooms, coffee shops, restaurants, climbing gym, Chicago river boathouses, and more!  The house has been very well maintained and is move-in ready!\n",
                    "Chicago, IL 60618",
                    4600);

            Property property3 = new Property(
                    "1837 N Fremont St",
                    3795000,
                    "Welcome to this architectural masterpiece, nestled in the heart of Lincoln Park on the serene, tree-lined Fremont Street. This spacious, 5-bedroom contemporary haven has been thoughtfully remodeled by renowned designer Donna Mondi, seamlessly blending elegance, modern design, and meticulous attention to detail to create an unparalleled living experience.  From the moment you step inside, you're welcomed by soaring ceilings and an abundance of natural light pouring through two-story front windows, complete with electric-controlled shades that offer the perfect balance of privacy and illumination. The living room radiates warmth with its inviting fireplace, setting the tone for a space that effortlessly flows into the state-of-the-art kitchen.  In this culinary haven, the stunning custom Arabescato Corchia marble island takes center stage, complemented by a handcrafted vintage brass hood and an Italian-inspired water line for cookware. Premium Miele appliances, including an integrated coffee machine, enhance the space, while hand-polished lacquer cabinets exude sophistication. This kitchen is the perfect fusion of beauty and functionality, ideal for both casual gatherings and elegant entertaining.  The dining area, complete with its own fireplace, offers a serene setting to host friends and family. The NanaWall glass door system opens to a tranquil Japanese garden fountain, merging indoor elegance with outdoor serenity. The main level also features a beautifully redesigned powder bath with custom Venetian plaster and a sleek steel and frosted glass sliding door. On the second level, you'll find three generously sized bedrooms, one currently used as a den, along with a spacious bathroom that includes a dual vanity and a walk-in shower. This level also offers a convenient laundry room.  The third level is dedicated entirely to a tranquil primary suite, offering incredible natural light and treetop views of Fremont Street. The spacious walk-in closet, outfitted with custom shelving and drawers, provides ample storage, while a second washer/dryer adds ultimate convenience. The spa-like ensuite bathroom features floating dual vanities and a steam shower, creating a sanctuary of relaxation. Step outside to your private rooftop terrace complete with a custom stainless steel chef's kitchen perfect for outdoor dining. Adorned with vibrant perennials and offering panoramic views of the city skyline, this space also has the ability to accommodate solar panels. A convenient dumbwaiter connects this rooftop paradise to the main kitchen below, enhancing both luxury and functionality.  The lower level offers an expansive family room, a climate-controlled wine cellar, and a private guest suite, ensuring comfort and elegance for every family member or guest. Throughout the home, wide plank walnut flooring and heated floors add warmth and sophistication. The Savant smart system seamlessly controls lighting, climate across seven zones, and entertainment, elevating your home life experience to the next level. Completing this exceptional property is a large, detached 2-car garage with radiant heated floors and an additional rooftop deck, perfect for enjoying the outdoors or gardening. A majestic maple tree graces the front, enhancing the home's curb appeal. Situated just steps from vibrant dining, shopping, and entertainment, this contemporary home on Fremont Street embodies luxury and comfort living at its best.\n",
                    "Chicago, IL 60614",
                    4662);

            Property property4 = new Property(
                    "2818 W Wellington Ave",
                    899000,
                    "Experience Unparalleled Luxury! Welcome to this stunning, one-of-a-kind luxury home, where elegance meets modern comfort. Nestled in a prestigious neighborhood, this 3-bedroom, 3.5-bathroom masterpiece boasts breathtaking architecture, high-end finishes, and exceptional craftsmanship. Key Features: Grand entrance with high ceilings & beautiful chandelier. Expansive open concept living space with glass doors. Gourmet chef's kitchen with state-of-the-art appliances, custom cabinetry and more. Then step into the beautiful backyard, a perfect blend of luxury, relaxation, and entertainment. Whether you're hosting gatherings, enjoying a quiet evening, or creating lasting memories, this outdoor has it all! Prime Location: Located in Avondale Neighborhood, this home offers exclusivity, privacy, and convenience-just minutes away from the express ways I90/94, public transportation, great schools, fine dining, and entertainment. Schedule your private showing today and be impressed.\n",
                    "Chicago, IL 60618",
                    3000);

            Property property5 = new Property(
                    "3454 W Potomac Ave",
                    959000,
                    "Modern 6 bed, 4.1 bath new construction home on an oversized 30' corner lot! Best price/sf of a new house in the area!  Extra wide floor plan with great natural light. Contemporary black and white color palette with natural wood accents throughout. Open concept main level perfect for entertaining. Spacious combined living/dining area with built-in banquet. Chef's kitchen with striking floor to ceiling black cabinetry and quartz counters, Cafe appliances, large center island with waterfall counter and bar seating, and walk-in pantry. Adjoining family room with patio doors to back deck. Sun-filled primary suite with huge built-out WIC and private bath featuring a frameless glass shower, freestanding tub, and dual sinks. Three additional bedrooms, one with ensuite bath, the other two with shared jack and Jill bath + laundry complete the upper level. Finished lower level includes a spacious recreation room with full wet bar, two bedrooms, full bath, second laundry, extra storage space, and utilities. Great outdoor space! Fully enclosed yard with back deck and detached two-car garage. Conveniently located near restaurants, entertainment, shopping, public transportation and 200-acre Humboldt Park!\n",
                    "Chicago, IL 60651",
                    4098);

            Property property6 = new Property(
                    "461 W Melrose St",
                    3300000,
                    "East Lakeview is the setting for this gorgeous contemporary masterpiece. The home is comprised of four levels of living space. The spaces are all accessible by an elevator that can transport you from the basement to the roof and all levels in between. There is a beautiful and spacious living room that combines and flows with the large dining room. The kitchen is  a chef's dream and perfect for those who like to cook, entertain or simply enjoy such wide open space. The kitchen has an island with a beautiful custom made marble countertop, high end appliances, pantry, professional range hood, and custom cabinets. The kitchen has various ceiling heights including an area where it reaches upwards of twenty feet, giving the area sense of unmatched volume. The first floor is complimented by an open family room. The rest of the home includes six ample bedrooms, three full baths and two half baths. The generous sized private primary suite has a large walk in closet leading to one of several outside decks. It is complimented by a complete bathroom including all modern amenities. The  full finished basement has high ceilings and perfectly equipped for your relaxation. Outside is a beautiful fully fenced back yard just waiting for your outside enjoyment. The sellers have made many improvements which include- newer kitchen, new flooring, new windows, newer mechanicals, new light fixtures, custom closets, new drainage and tile in the backyard, heated gutters and downspouts, and high end appliances. All this plus, RM6 zoning, highly coveted parking spaces, and within a half block to Lake Shore drive and the incomparable Lake Michigan. This is truly a gem. Hurry !!!\n",
                    "Chicago, IL 60657",
                    5400);

            Property property7 = new Property(
                    "1741 N Mozart St",
                    849000,
                    "Reimagined Logan Square single-family home that's been carefully curated by the owner/designer. Three bedrooms up, with a large primary suite, full finished lower level with bedroom, & family room. The living floor has high ceilings, oversized windows, and sliders to the new back deck, allowing for indoor/outdoor living. Large open kitchen with updated quartz countertops, lighting ,and stainless appliance package. Huge dining room with design-forward lighting and fireplace. Charming front porch, 2.5 car garage and a few steps from the 606. Open this Saturday March 29th from 2p to 5pm and Sunday March 30th from 11a to 2p. See additional info for FAQ sheet\n",
                    "Chicago, IL 60647",
                    2631);

            Property property8 = new Property(
                    "2317 W Ohio St",
                    949000,
                    "Fully gut-rehabbed in 2022, this 4 bedroom, 3.1 bath, 3000 SQFT single family home ideally located in Chicago's Ukrainian Village neighborhood. Flooded with natural light, the main level's open floor plan flows seamlessly and creates the ideal space for both entertaining and everyday living. The eat-in kitchen features an expansive center island with breakfast bar, Bosch appliances, and copious cabinet and counter space. A family room off the kitchen leads to a back deck and the backyard, ideal for grilling, relaxing, and dining al fresco. Upstairs on the second floor, three bedrooms, 2 full baths, and a laundry room are conveniently located on the same level. The lower level has a recreation room, a 4th bedroom, and a full bath. An all brick 2-car garage with roof deck was added to the property in 2022 and provides an urban oasis for relaxation and entertainment purposes. Ideally located near all Ukrainian Village has to offer and a short ride to the vibrant West Loop area. Close to public transportation and one block from Mitchell School.\n",
                    "Chicago, IL 60612",
                    3000);

            Property property9 = new Property(
                    "1701 N Dayton St",
                    4750000,
                    "Stunning LG custom-built single-family home on an extra wide 36.5' lot on a quiet stretch of Dayton in the heart of Lincoln Park. The classic red brick and limestone facade with bluestone steps to a carved walnut door welcomes you into this sweeping home that offers relaxed elegance with exceptional millwork, timeless finishes, and a thoughtful floor plan.    The elevated entryway features an oval tray ceiling and a symmetrical inlaid floor medallion inviting you into the gracious living room with a marble fireplace and adjoining ample dining room and butler's wet bar complete with a polished stainless steel sink, lined silver drawers, and a spacious walk-in pantry. The main gathering space of the first floor is the gourmet kitchen, boasting an angled coffered ceiling, double islands with Shaws' Farmhouse sinks, a mirrored Sub Zero refrigerator, and top-of-the-line Wolf appliances, including a wall oven, warming drawer, and side-by-side double oven with a griddle. Built-ins abound, plus a breakfast banquette that offers ample space for dining. The adjacent family room features a coffered ceiling and a grand oversized marble fireplace, flanked by paneled bookcases and more built-ins for the reading area and workstation. French doors with seeded leaded glass window transoms lead to a bluestone patio with a seating area and built-in DCS grill, providing a seamless transition to outdoor entertaining on the full-width 640 sqft Trex deck with a pergola. and a generously sized family room featuring an oversized marble fireplace and reading area. The first-floor features hickory, hand-scraped, and beveled hardwood floors, extensive millwork, crown molding, and 8 1/2 baseboard trim\n",
                    "Chicago, IL 60614",
                    8000);

            Property property10 = new Property(
                    "334 N Jefferson St UNIT D",
                    925000,
                    "This rare corner townhome in Kinzie Station offers 3 bedrooms and 3 baths, showcasing a renovated kitchen and an open-concept design. With a bright southeast exposure, natural light pours into the home, complemented by multiple private outdoor spaces with stunning city views.    The main level features a marble entryway and a versatile bedroom with an ensuite bath-ideal for a guest room, home office, or playroom. Upstairs, the primary suite boasts a walk-in closet and a beautifully updated bathroom with dual sinks. A second bedroom, a refreshed bathroom, and a laundry area complete this floor.    The sun-drenched third level highlights a spacious living room with a gas fireplace, elegant Brazilian Cherry hardwood floors, a dedicated dining area, and a balcony. The sleek white kitchen is equipped with a Viking stove, Sub-Zero refrigerator, expansive countertops, and a large breakfast bar.    On the fourth floor, a generous family room with a wet bar opens to an enormous private roof deck, complete with a gas line for grilling and built-in surround sound-perfect for entertaining or unwinding. Flexible spaces throughout the home allow for an office or a potential fourth bedroom. The property also includes a fenced yard and access to a private association parking lot, ideal for outdoor activities.    Residents enjoy access to amenities in the adjacent condo building, including a newly updated gym, security guard service, and secure package delivery. With FOUR parking spaces (garage, driveway, and two in the adjacent lot), this home offers both convenience and luxury. Experience peaceful, residential living just moments from the Loop, River North, and Fulton Market, with easy access to dining, shopping, parks, and the 90/94 expressway.\n",
                    "Chicago, IL 60661",
                    2600);

            Property property11 = new Property(
                    "1249 S Plymouth Ct",
                    1200000,
                    "Welcome home to your urban oasis in this rarely available single family home in Chicago's South Loop! Located in Dearborn Park II, on a quiet tree-lined street directly across from lovely Mary Richardson Jones Park and highly rated South Loop Elementary, this 3000+ sqft home has three levels & offers three bedrooms and three and a half newly renovated bathrooms and so much more. This bright and sunny home features hardwood flooring thru-out the main levels and a carpeted cozy basement level. Enjoy sunny western & park views from the spacious and open living/dining room combo. Try your culinary skills in the large, renovated kitchen featuring high-end Viking & Thermador appliances, granite countertops & custom cabinetry. Grow your herbs year round in the kitchen's charming all-glass garden window. Step down from the kitchen into the bright & spacious family room which features high ceilings, built-in oak shelving and marble surrounded wood-burning fireplace. With various floor plans on the block, the top level of this home features THREE spacious bedrooms, two full baths and a full size side-by-side washer/dryer. The bright and spacious primary bedroom has oak flooring, two large closets and overlooks the rear patio with skyline views! The lower level can be converted to a 4th bedroom or in-law arrangement and has a new full bathroom, an extra large refinished office area which easily could be converted to suit your needs and a huge additional storage room. The roof is less than 10yrs old! The two-car detached parking garage includes plenty of storage space and leads to a gated interior alleyway shared by the association. Steps from Grant Park, Michigan Avenue shops, Museum Campus, lakefront trail and beaches, restaurants, Jewel, Target, Trader Joe's and convenient access to Loop, Lake Shore Drive, U of C shuttle, expressways and public transit.\n",
                    "Chicago, IL 60605",
                    3000);

            Property property12 = new Property(
                    "2779 N Kenmore Ave",
                    1300000,
                    "Unicorn alert! This rare, beautifully renovated Victorian in the heart of Lincoln Park seamlessly blends historic craftsmanship with contemporary elegance, offering a truly one-of-a-kind living experience. With 4 bedrooms and 3 levels of living space above grade, this home is truly a rare find. No detail was overlooked in this full gut renovation, preserving stunning original features like stained glass windows, vintage banisters, and intricate tile work while introducing modern comforts. The main level offers the perfect balance between an open, flowing layout and defined spaces, featuring custom built-ins, a fireplace, 6 engineered white oak floors\n",
                    "Chicago, IL 60614",
                    2532);

            Property property13 = new Property(
                    "4425 N Winchester Ave",
                    1125000,
                    "Lovely Victorian home on large lot in Ravenswood. This very wide home offers formal foyer, living and dining rooms. 10' ceilings with over sized windows maximizes the light of the house. 2 extra rooms that could be offices/homework room/game room/bedroom both with closets. Full bath with walk in shower plus storage. Amazing kitchen/ great room with breakfast area and built in pantry. Gas fireplace with blower. Sleek maple kitchen with Verde Jaco Italian granite. Upstairs are 3 good size bedrooms with maples and new carpeted floors. Lots of nooks and crannies to discover. Lower level offers newly painted floors and walls making a great space for exercise/mud room/ storage. Plus the original grandfathers workshop with tool bench. Side entrance easy access. And then there is the oversized landscaped yard with wisteria bush, sunset maple, and ornamental pear trees that create the most wonderful setting. Back deck with pergola for morning coffee or evening dinners. Huge front porch offers neighborly hellos on this wonderful block. New painted interior, new carpet, and new light fixtures are some of the recent upgrades. Hardie board siding. Two car garage with side parking pad. Lycee just 1/2 block away. Montrose el stop 2 blocks. Plus all the shopping and restaurants that Montrose has to offer. This home has so much space both inside and out. Don't miss the chance to call this one home.\n",
                    "Chicago, IL 60640",
                    3000);

            Property property14 = new Property(
                    "4511 N Saint Louis Ave",
                    889000,
                    "A perfect blend of traditional charm and modern elegance, this fully rebuilt contemporary home sits on a generous 38' wide lot, featuring a wrap-around porch and rear deck. The original structure was completely transformed with a brand-new second story and a 13' rear addition, creating a total of 3,213 sq. ft. of finished living space.    The open-concept first floor is designed for entertaining, boasting a half bath, a striking fireplace feature wall, and seamless flow between indoor and outdoor spaces. The spacious kitchen centers around a large island with clear sightlines to both the front and back patio doors, leading to a covered porch with tongue-and-groove ceilings and recessed lighting.    Upstairs, you'll find three bedrooms, including a luxurious primary suite with a soaking tub, separate shower, and walk-in closet. Two additional bedrooms share a beautifully designed second full bath. A convenient second-floor laundry room is also included.    The fully finished basement offers additional living space with a bonus room, a fourth bedroom or playroom, and a theater room/den with soaring 9' ceilings in the rear addition (with lower ceilings in the remaining basement area). The property also includes a detached two-car garage and an extra parking space beside it.    Wrapped in Hardie Board siding, this home is sits in a vibrant neighborhood surrounded by new construction.\n",
                    "Chicago, IL 60625",
                    3213);

            Property property15 = new Property(
                    "401 W Dickens Ave",
                    5995000,
                    "This ultra notable single-family home sits on an extra-wide lot at the corner of Sedgwick and Dickens! The setting of the home allows for open views outside. Every finish is high end with slab marble floors, beautiful hardwood floors, custom woodwork, and high-end built ins. Elevator to all levels, plus powder rooms at each entertaining level (three in total) for added convenience. Totally luxurious & custom finishes throughout. Enjoy beautiful formal spaces unique to the marketplace. The main living level offers large room sizes and access to the outside. Wonderful library/family room which walks out to the yard. Large kitchen and dining with amazingly sunny views. Primary bedroom with a dressing room, huge marble bath plus 3 additional bedroom suites. Glorious top floor atrium sunroom is the crown glory of the home with entire roof deck, fireplace and prime entertaining and living space. Attached 2 car garage plus driveway. Easy walk to Francis Parker and Latin schools, as well as Lincoln Elementary. Please ask about exclusions.\n",
                    "Chicago, IL 60614",
                    7252);

            Property property16 = new Property(
                    "339 W Webster Ave UNIT 2B",
                    1225000,
                    "Enjoy the perfect, prime East Lincoln Park location in this wonderfully intimate gated townhome community. This west facing, super sunny townhome lives like a single-family home, with four full floors of living space, a private rooftop deck, and one garage space to top it off! The main living level features beautiful living space, dining, plus an updated kitchen - an entertainer's dream with coffered ceilings and an abundance of windows. The all-white kitchen features Viking, Subzero, and Bosch appliances, along with ample cabinetry, quartz countertops and island seating. The next level up features two generously sized bedrooms that share a well-appointed bath, plus a laundry room. The top level is devoted entirely to the primary suite with a recently updated bathroom featuring double sinks, a walk-in shower, and a skylight for sensational natural light. Not to be missed is the gracious first floor living space with a wet sink/bar and wine fridge, walk out west facing landscaped patio, and an additional full bathroom. The private roof deck has views for days! Steps from Parker, the zoo, lake, amazing dining, plus Lincoln Schools, this is a fantastic opportunity to live in one the best Lincoln Park locales!\n",
                    "Chicago, IL 60614",
                    2400);

            Property property17 = new Property(
                    "1541 W Addison St",
                    1200000,
                    "Don't miss this pristine, beautifully rehabbed solid brick home just steps to the highly desirable Southport Corridor. A rare find, boasting 4 bedrooms, 3 1/2 baths and three outdoor spaces, including an amazing fenced backyard. Remarkable features include hardwood flooring, custom stainless kitchen with pantry and designer cabinets, great bathrooms including a custom primary ensuite, a huge lower-level family room, two car garage and new turf and irrigation system in fenced-in backyard. Everything has been redone in this home and don't let the address fool you - its super quiet inside! A jewel in Lakeview, the Southport Corridor thrives with culture, charm, and unique shopping, amazing dining and theatre. Grocery and public transport only a couple of blocks away make this location A+!\n",
                    "Chicago, IL 60613",
                    2869);

            PropertyImage propertyImage1 = new PropertyImage("1743280986563_8c1815366539cf90fd6cb38dbb1b1e1e-cc_ft_960.webp");
            PropertyImage propertyImage2 = new PropertyImage("1743280986566_3bd01c92edfab81e6ef7702df5c5f315-cc_ft_960.webp");
            PropertyImage propertyImage3 = new PropertyImage("1743280986566_419c22f5dd1ddc1a6d861df85c941db9-cc_ft_960.webp");
            PropertyImage propertyImage4 = new PropertyImage("1743280986569_e0eeefdb3c45f55b99d1ad272a9595c3-cc_ft_960.webp");
            PropertyImage propertyImage5 = new PropertyImage("1743280986573_b48577b143d2f0c5b79a4bb14dd7ec0d-cc_ft_960.webp");
            property1.addPropertyImage(propertyImage1);
            property1.addPropertyImage(propertyImage2);
            property1.addPropertyImage(propertyImage3);
            property1.addPropertyImage(propertyImage4);
            property1.addPropertyImage(propertyImage5);

            PropertyImage propertyImage6 = new PropertyImage("1743281104544_cc41995923da491a77509ba3c594dfbd-cc_ft_960.webp");
            PropertyImage propertyImage7 = new PropertyImage("1743281104544_dcfa791d9108ba2825906026396b1b06-cc_ft_960.webp");
            PropertyImage propertyImage8 = new PropertyImage("1743281104545_3d2b506d9411b5465ba886b4f8a5e236-cc_ft_960.webp");
            PropertyImage propertyImage9 = new PropertyImage("1743281104545_97f285258a02cc3c9fb1d378463266ce-cc_ft_960.webp");
            PropertyImage propertyImage10 = new PropertyImage("1743281104545_79540e8ca9aafe743e3c504a3fe95750-cc_ft_960.webp");

            property2.addPropertyImage(propertyImage6);
            property2.addPropertyImage(propertyImage7);
            property2.addPropertyImage(propertyImage8);
            property2.addPropertyImage(propertyImage9);
            property2.addPropertyImage(propertyImage10);

            PropertyImage propertyImage11 = new PropertyImage("1743281271085_cbd7103d0fafdb12e7debbc0654493bd-cc_ft_960.webp");
            PropertyImage propertyImage12 = new PropertyImage("1743281271086_3d168d1493c8661b69c87cc55d6099d1-cc_ft_960.webp");
            PropertyImage propertyImage13 = new PropertyImage("1743281271086_5d44f3575f04874e53743a5c1718ca88-cc_ft_960.webp");
            PropertyImage propertyImage14 = new PropertyImage("1743281271087_2c9fade74be1b69ca7c7cb273c6c87c2-cc_ft_960.webp");
            PropertyImage propertyImage15 = new PropertyImage("1743281271087_79cac6350cb9217e7a6bcc8f5d5fd0cb-cc_ft_960.webp");
            PropertyImage propertyImage16 = new PropertyImage("1743281271087_978969480d1b891918b174eb749a651d-cc_ft_960.webp");
            PropertyImage propertyImage17 = new PropertyImage("1743281271088_0bd9f82b77c94edc082bb39408007973-cc_ft_960.webp");

            property3.addPropertyImage(propertyImage11);
            property3.addPropertyImage(propertyImage12);
            property3.addPropertyImage(propertyImage13);
            property3.addPropertyImage(propertyImage14);
            property3.addPropertyImage(propertyImage15);
            property3.addPropertyImage(propertyImage16);
            property3.addPropertyImage(propertyImage17);

            PropertyImage propertyImage18 = new PropertyImage("1743301338472_33a4f1cbdd5fb813e4696238006b7882-cc_ft_960.webp");
            PropertyImage propertyImage19 = new PropertyImage("1743301338476_0ce73eb7b856382475f51f49f79beb89-cc_ft_960.webp");
            PropertyImage propertyImage20 = new PropertyImage("1743301338476_78dd178cbaaa151772e98ddc8f157b61-cc_ft_960.webp");
            PropertyImage propertyImage21 = new PropertyImage("1743301338477_28dffdb73243072752daf3888d06e47a-cc_ft_960.webp");
            PropertyImage propertyImage22 = new PropertyImage("1743301338477_cbf60776fd416a41ea66240135d6edbe-cc_ft_960.webp");
            PropertyImage propertyImage23 = new PropertyImage("1743301338477_dd32b92ceb6814bda0aa4e5b072887df-cc_ft_960.webp");

            property4.addPropertyImage(propertyImage18);
            property4.addPropertyImage(propertyImage19);
            property4.addPropertyImage(propertyImage20);
            property4.addPropertyImage(propertyImage21);
            property4.addPropertyImage(propertyImage22);
            property4.addPropertyImage(propertyImage23);

            PropertyImage propertyImage24 = new PropertyImage("1743301467429_a0596dd36e88cac9a64f35eaf1d8ce71-cc_ft_960.webp");
            PropertyImage propertyImage25 = new PropertyImage("1743301467429_e433d1c2b82fd5ca765a330996a33b8b-cc_ft_960.webp");
            PropertyImage propertyImage26 = new PropertyImage("1743301467430_35771fac6f65e303b0aae04797434661-cc_ft_960.webp");
            PropertyImage propertyImage27 = new PropertyImage("1743301467430_383e06b5c9eaa15362db824c9315bb48-cc_ft_960.webp");
            PropertyImage propertyImage28 = new PropertyImage("1743301467430_445fa915603a0172fcc33ab2f4bffff5-cc_ft_960.webp");

            property5.addPropertyImage(propertyImage24);
            property5.addPropertyImage(propertyImage25);
            property5.addPropertyImage(propertyImage26);
            property5.addPropertyImage(propertyImage27);
            property5.addPropertyImage(propertyImage28);

            PropertyImage propertyImage29 = new PropertyImage("1743301628478_038333ffc0b2ab9b3d4beee098815cd1-cc_ft_960.webp");
            PropertyImage propertyImage30 = new PropertyImage("1743301628479_e89f81a9e0c787d35f53786a26bf94db-cc_ft_960.webp");
            PropertyImage propertyImage31 = new PropertyImage("1743301628480_4575349ee82303eddef14e866c03d9b6-cc_ft_960.webp");
            PropertyImage propertyImage32 = new PropertyImage("1743301628480_902a62e40dfe6025619ad874c015e85e-cc_ft_960.webp");
            PropertyImage propertyImage33 = new PropertyImage("1743301628481_7445e48b3fef12706bda016fed9ead39-cc_ft_960.webp");
            PropertyImage propertyImage34 = new PropertyImage("1743301628481_8e1830db57e8855cc40ab1f82e7b9ddf-cc_ft_960.webp");
            PropertyImage propertyImage35 = new PropertyImage("1743301628481_9945c420965cf28e99533c53f919c2a8-cc_ft_960.webp");
            PropertyImage propertyImage36 = new PropertyImage("1743301628482_4f93514962a2d6df83b282589ace1ba1-cc_ft_960.webp");

            property6.addPropertyImage(propertyImage29);
            property6.addPropertyImage(propertyImage30);
            property6.addPropertyImage(propertyImage31);
            property6.addPropertyImage(propertyImage32);
            property6.addPropertyImage(propertyImage33);
            property6.addPropertyImage(propertyImage34);
            property6.addPropertyImage(propertyImage35);
            property6.addPropertyImage(propertyImage36);

            PropertyImage propertyImage37 = new PropertyImage("1743301809073_6002383c7e5af148dbf7865834c9d978-cc_ft_960.webp");
            PropertyImage propertyImage38 = new PropertyImage("1743301809073_677b8026a3951bb4dddd94cc39af7c39-cc_ft_960.webp");
            PropertyImage propertyImage39 = new PropertyImage("1743301809074_4619a01b6891e0801336c0705ab189ef-cc_ft_960.webp");
            PropertyImage propertyImage40 = new PropertyImage("1743301809074_a12a500ba769699bc054ea0ce703e52c-cc_ft_960.webp");
            PropertyImage propertyImage41 = new PropertyImage("1743301809074_cac06501b21dfee3dc57906057bcc348-cc_ft_960.webp");
            PropertyImage propertyImage42 = new PropertyImage("1743301809075_0e9427333a0d908161e5147b7dc123ee-cc_ft_960.webp");

            property7.addPropertyImage(propertyImage37);
            property7.addPropertyImage(propertyImage38);
            property7.addPropertyImage(propertyImage39);
            property7.addPropertyImage(propertyImage40);
            property7.addPropertyImage(propertyImage41);
            property7.addPropertyImage(propertyImage42);

            PropertyImage propertyImage43 = new PropertyImage("1743302048220_02499d07b48dce810e28dea78a5e026f-cc_ft_960.webp");
            PropertyImage propertyImage44 = new PropertyImage("1743302048220_d420e4ce5c58d33e2b36b4e90fb5c56d-cc_ft_960.webp");
            PropertyImage propertyImage45 = new PropertyImage("1743302048221_88838f14535dd65a2cb177a59669b281-cc_ft_960.webp");
            PropertyImage propertyImage46 = new PropertyImage("1743302048223_eddb73c7118ac6f157503cc1218ab8e5-cc_ft_960.webp");
            PropertyImage propertyImage47 = new PropertyImage("1743302048225_4d8c640575f652c14501094280ca42b3-cc_ft_960.webp");
            PropertyImage propertyImage48 = new PropertyImage("1743302048225_53c34c82cf7f09148a98c1ad4bf8fdd2-cc_ft_960.webp");

            property8.addPropertyImage(propertyImage43);
            property8.addPropertyImage(propertyImage44);
            property8.addPropertyImage(propertyImage45);
            property8.addPropertyImage(propertyImage46);
            property8.addPropertyImage(propertyImage47);
            property8.addPropertyImage(propertyImage48);

            PropertyImage propertyImage49 = new PropertyImage("1743302255449_cf806ba37db343ee7f0ee51d39252135-cc_ft_960.webp");
            PropertyImage propertyImage50 = new PropertyImage("1743302255450_02be17d8f9ac024cf482014e592e180d-cc_ft_960.webp");
            PropertyImage propertyImage51 = new PropertyImage("1743302255450_4cbdcdfef821e5dc91e7751514c6d68e-cc_ft_960.webp");
            PropertyImage propertyImage52 = new PropertyImage("1743302255450_f957316337dc3918f40f7cd7383eecbf-cc_ft_960.webp");
            PropertyImage propertyImage53 = new PropertyImage("1743302255451_77be1d31e0ad3589d2bb47f7f85b924f-cc_ft_960.webp");
            PropertyImage propertyImage54 = new PropertyImage("1743302255451_8eb8c6eceb0e3bea3cdb9c2e9793d4e5-cc_ft_960.webp");
            PropertyImage propertyImage55 = new PropertyImage("1743302255451_b31d1be54f40e40242f5d5596252db0c-cc_ft_960.webp");

            property9.addPropertyImage(propertyImage49);
            property9.addPropertyImage(propertyImage50);
            property9.addPropertyImage(propertyImage51);
            property9.addPropertyImage(propertyImage52);
            property9.addPropertyImage(propertyImage53);
            property9.addPropertyImage(propertyImage54);
            property9.addPropertyImage(propertyImage55);

            PropertyImage propertyImage56 = new PropertyImage("1743384015245_6a9c75de2a1a1b59e7ffb67666e34f21-cc_ft_960.webp");
            PropertyImage propertyImage57 = new PropertyImage("1743384015263_c8b7f3395049c85c63629545f9b7c628-cc_ft_960.webp");
            PropertyImage propertyImage58 = new PropertyImage("1743384015264_4208506e92354c9bbf08264f52214443-cc_ft_960.webp");
            PropertyImage propertyImage59 = new PropertyImage("1743384015264_57e9193e3d81780a668bc762f2662ce1-cc_ft_960.webp");
            PropertyImage propertyImage60 = new PropertyImage("1743384015265_feea5afd5f1e1d5ac49cbabb52ab9de0-cc_ft_960.webp");
            PropertyImage propertyImage61 = new PropertyImage("1743384015266_f1ab08cd9526db6b89869706e2326957-cc_ft_960.webp");

            property10.addPropertyImage(propertyImage56);
            property10.addPropertyImage(propertyImage57);
            property10.addPropertyImage(propertyImage58);
            property10.addPropertyImage(propertyImage59);
            property10.addPropertyImage(propertyImage60);
            property10.addPropertyImage(propertyImage61);

            PropertyImage propertyImage62 = new PropertyImage("1743384199295_4542114353ac37b6202a6de33663be81-cc_ft_960.webp");
            PropertyImage propertyImage63 = new PropertyImage("1743384199295_a55bd3c3af04d6f7ac3807af405524ae-cc_ft_960.webp");
            PropertyImage propertyImage64 = new PropertyImage("1743384199296_210172c41c26c079efc76d51d593a82b-cc_ft_960.webp");
            PropertyImage propertyImage65 = new PropertyImage("1743384199296_39dc5cf00542133c4df96861e635f7a7-cc_ft_960.webp");
            PropertyImage propertyImage66 = new PropertyImage("1743384199296_8dc571649cf4b989fd10e1fda3aa1419-cc_ft_960.webp");
            PropertyImage propertyImage67 = new PropertyImage("1743384199297_055b93946d56b5a3f21453e95116e71c-cc_ft_960.webp");
            PropertyImage propertyImage68 = new PropertyImage("1743384199297_4fe190c6aef86b5e0aaa0b4fe47ae719-cc_ft_960.webp");

            property11.addPropertyImage(propertyImage62);
            property11.addPropertyImage(propertyImage63);
            property11.addPropertyImage(propertyImage64);
            property11.addPropertyImage(propertyImage65);
            property11.addPropertyImage(propertyImage66);
            property11.addPropertyImage(propertyImage67);
            property11.addPropertyImage(propertyImage68);

            PropertyImage propertyImage69 = new PropertyImage("1743384392201_24210295f2d05ba693adc25d1c896206-cc_ft_960.webp");
            PropertyImage propertyImage70 = new PropertyImage("1743384392202_2b4d95e3030041acff0ccb66cd4bfd36-cc_ft_960.webp");
            PropertyImage propertyImage71 = new PropertyImage("1743384392202_2b59c2456dce88ba36b7bcfbbdf0f85f-cc_ft_960.webp");
            PropertyImage propertyImage72 = new PropertyImage("1743384392202_4a12de4cf2f21dea63628868986ae31b-cc_ft_960.webp");
            PropertyImage propertyImage73 = new PropertyImage("1743384392203_240789fdb44d85f5c5a8a72de4db20e3-cc_ft_960.webp");
            PropertyImage propertyImage74 = new PropertyImage("1743384392203_4c55a56e9e17f85f2ec6eeb0086ec5d0-cc_ft_960.webp");
            PropertyImage propertyImage75 = new PropertyImage("1743384392204_db5691f807cab5e67c31f141fc3f6ac7-cc_ft_960.webp");

            property12.addPropertyImage(propertyImage69);
            property12.addPropertyImage(propertyImage70);
            property12.addPropertyImage(propertyImage71);
            property12.addPropertyImage(propertyImage72);
            property12.addPropertyImage(propertyImage73);
            property12.addPropertyImage(propertyImage74);
            property12.addPropertyImage(propertyImage75);

            PropertyImage propertyImage76 = new PropertyImage("1743387717563_1b52d279e85865c7f0bf096a15389cde-cc_ft_960.webp");
            PropertyImage propertyImage77 = new PropertyImage("1743387717569_3bd5e5aa31981523260b921b90147af1-cc_ft_960.webp");
            PropertyImage propertyImage78 = new PropertyImage("1743387717569_ef8752d95d4a035171a9e68ea1b931b3-cc_ft_960.webp");
            PropertyImage propertyImage79 = new PropertyImage("1743387717570_a1002e32f5837499940cb388fd9564e9-cc_ft_960.webp");
            PropertyImage propertyImage80 = new PropertyImage("1743387717571_fb9d6f09ff74869a68e130639b4a6656-cc_ft_960.webp");
            PropertyImage propertyImage81 = new PropertyImage("1743387717572_4e76c9ea759f17c5117146b8e3767d59-cc_ft_960.webp");

            property13.addPropertyImage(propertyImage76);
            property13.addPropertyImage(propertyImage77);
            property13.addPropertyImage(propertyImage78);
            property13.addPropertyImage(propertyImage79);
            property13.addPropertyImage(propertyImage80);
            property13.addPropertyImage(propertyImage81);

            PropertyImage propertyImage82 = new PropertyImage("1743387872830_3d90623319bb81e1fc9981458177dfbe-cc_ft_960.webp");
            PropertyImage propertyImage83 = new PropertyImage("1743387872831_52a0b90d5ad6706b893bde00716c33d3-cc_ft_960.webp");
            PropertyImage propertyImage84 = new PropertyImage("1743387872832_02f4e0b3bf227e23803789cc22b57a51-cc_ft_960.webp");
            PropertyImage propertyImage85 = new PropertyImage("1743387872832_09ed819d77efdcc01b00c5aabbaea793-cc_ft_960.webp");
            PropertyImage propertyImage86 = new PropertyImage("1743387872832_1dbb545e72db17e3e4f71782d804f150-cc_ft_960.webp");
            PropertyImage propertyImage87 = new PropertyImage("1743387872833_9f3d06c29900d8b66f2b0cb1711621e6-cc_ft_960.webp");
            PropertyImage propertyImage88 = new PropertyImage("1743387872833_d6d09d6b56a9ada275beac058cd819ea-cc_ft_960.webp");

            property14.addPropertyImage(propertyImage82);
            property14.addPropertyImage(propertyImage83);
            property14.addPropertyImage(propertyImage84);
            property14.addPropertyImage(propertyImage85);
            property14.addPropertyImage(propertyImage86);
            property14.addPropertyImage(propertyImage87);
            property14.addPropertyImage(propertyImage88);

            PropertyImage propertyImage89 = new PropertyImage("1743388060454_c24b273254041b893ccd6692111b5200-cc_ft_960.webp");
            PropertyImage propertyImage90 = new PropertyImage("1743388060455_79189db1b8f663642e731eb0b01b5f39-cc_ft_960.webp");
            PropertyImage propertyImage91 = new PropertyImage("1743388060456_08d55c9fc87e4f22d67a478fab4c9a03-cc_ft_960.webp");
            PropertyImage propertyImage92 = new PropertyImage("1743388060456_7e771c0fb66cd751539d8d806f0ba148-cc_ft_960.webp");
            PropertyImage propertyImage93 = new PropertyImage("1743388060457_0164127f87e51ea7a12bd2c305b6c2e0-cc_ft_960.webp");
            PropertyImage propertyImage94 = new PropertyImage("1743388060457_a7cef31db25ea18719d4733f0b95b223-cc_ft_960.webp");
            PropertyImage propertyImage95 = new PropertyImage("1743388060457_ee5061af173e2bf0aad53eb5f26cf706-cc_ft_960.webp");

            property15.addPropertyImage(propertyImage89);
            property15.addPropertyImage(propertyImage90);
            property15.addPropertyImage(propertyImage91);
            property15.addPropertyImage(propertyImage92);
            property15.addPropertyImage(propertyImage93);
            property15.addPropertyImage(propertyImage94);
            property15.addPropertyImage(propertyImage95);

            PropertyImage propertyImage96 = new PropertyImage("1743992061456_5817c06a023247878cad80b13437a344-cc_ft_960.webp");
            PropertyImage propertyImage97 = new PropertyImage("1743992061461_7949f19564896909d78f3fcaff95b1fd-cc_ft_960.webp");
            PropertyImage propertyImage98 = new PropertyImage("1743992061461_f16b81fd636cc523ace9d3bb0256552d-cc_ft_960.webp");
            PropertyImage propertyImage99 = new PropertyImage("1743992061462_f727e2747ba462aa10058f56caa3557e-cc_ft_960.webp");
            PropertyImage propertyImage100 = new PropertyImage("1743992061464_1fd8ed76ab9561d8997f2021f2fa36be-cc_ft_960.webp");
            PropertyImage propertyImage101 = new PropertyImage("1743992061464_eebaa9c584979606b61e3357c48d8547-cc_ft_960.webp");
            PropertyImage propertyImage102 = new PropertyImage("1743992061465_30d9ae794d238c468c93c24e75f606ec-cc_ft_960.webp");

            property16.addPropertyImage(propertyImage96);
            property16.addPropertyImage(propertyImage97);
            property16.addPropertyImage(propertyImage98);
            property16.addPropertyImage(propertyImage99);
            property16.addPropertyImage(propertyImage100);
            property16.addPropertyImage(propertyImage101);
            property16.addPropertyImage(propertyImage102);

            PropertyImage propertyImage103 = new PropertyImage("1743992242724_0ca592349432a46fba99299c74267220-cc_ft_960.webp");
            PropertyImage propertyImage104 = new PropertyImage("1743992242724_2ae7e5f1f89fcfb262f2088c53cce3b9-cc_ft_960.webp");
            PropertyImage propertyImage105 = new PropertyImage("1743992242725_50e9c8299c9368422483ba4dff253098-cc_ft_960.webp");
            PropertyImage propertyImage106 = new PropertyImage("1743992242725_54ea1b70cede8d606beecadf2b9c2f28-cc_ft_960.webp");
            PropertyImage propertyImage107 = new PropertyImage("1743992242726_9ff251395613a68239c6820f4daea338-cc_ft_960.webp");
            PropertyImage propertyImage108 = new PropertyImage("1743992242727_1376aeceae202e2bad77219dc8190cdd-cc_ft_960.webp");

            property17.addPropertyImage(propertyImage103);
            property17.addPropertyImage(propertyImage104);
            property17.addPropertyImage(propertyImage105);
            property17.addPropertyImage(propertyImage106);
            property17.addPropertyImage(propertyImage107);
            property17.addPropertyImage(propertyImage108);

            propertyRepository.saveAll(List.of(property1, property2, property3,
                    property4, property5, property6, property7, property8, property9,
                    property10, property11, property12, property13, property14,
                    property15, property16, property17));

            System.out.println("🟢 Properties and Property Images inserted into database.");

        }
    }
}