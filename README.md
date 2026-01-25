# Mobile_App-Asystent-Ekologiczny
Mobile app for subject "Podstawy programowania na platformę Android" na UZ 3rd year SSI

Sprint 1: Struktura aplikacji i podstawowa obsługa produktów
Obowiązkowe:
• Ekran główny z dolną nawigacją.
Utworzyć ekran główny aplikacji z dolnym paskiem nawigacyjnym
zawierającym 4 zakładki: Produkty, Kaucja, Raporty, Ustawienia. Każda
zakładka powinna otwierać osobny Fragment, zmieniający zawartość głównego
kontenera bez restartu aktywności. Nawigacja musi działać płynnie i wizualnie
wskazywać aktywną zakładkę.
• Formularz dodawania produktu
Utworzyć ekran z formularzem do dodawania produktów zawierający pola:
nazwa produktu, cena, data ważności, kategoria, opis produktu, sklep gdzie
produkt został zakupiony, data zakupu. Po naciśnięciu przycisku „Zapisz” dane
muszą być walidowane (puste pola, poprawny format liczbowy) i zapisane do
lokalnej bazy SQLite.
• Lista produktów
Utworzyć widok listy, w którym każdy element wyświetla: nazwę, cenę, kategorię
i datę ważności. Produkty, których data ważności minęła, mają być
automatycznie podświetlane na czerwono.
• Ekran szczegółów produktu
Po kliknięciu w dowolny produkt z listy ma otworzyć się nowy z pełnymi
informacjami o danym produkcie. Na ekranie szczegółów wyświetlane są
wszystkie pola, a użytkownik nie może ich edytować.

Dodatkowe I:
• Widok siatki (GridLayoutManager)
umożliwić przełączanie listy produktów z widoku listy na siatkę (2 kolumny)
poprzez ikonę w pasku narzędzi.
• Animacja fade-in przy dodaniu produktu
po zapisaniu nowego produktu element powinien łagodnie pojawić się na liście.
• Ikona kategorii obok nazwy [PARTIALLY DONE]
w każdym elemencie listy dodać małą ikonę graficzną symbolizującą kategorię
(np. warzywa, nabiał, napoje).
• Sortowanie po cenie
przycisk w menu powinien sortować produkty rosnąco lub malejąco według
ceny.

Dodatkowe II:
• Licznik produktów w nagłówku
w nagłówku listy ma być widoczna liczba wszystkich zapisanych produktów,
automatycznie aktualizowana.
• Pasek wyszukiwania
umożliwić wyszukiwanie produktów po nazwie z dynamicznym filtrowaniem
listy.
• Tooltip przy dłuższym przytrzymaniu [NOT DONE]
przy dłuższym dotknięciu produktu wyświetla się dymek z informacją o dacie
zakupu.
• Tryb ciemny/jasny [PARTIALLY DONE]
umożliwić zmianę motywu aplikacji z jasnego na ciemny.

Sprint 2: Operacje na produktach i walidacja
Obowiązkowe:
• Edycja produktu
Dodać możliwość modyfikowania istniejących produktów. Po wybraniu opcji
„Edytuj” użytkownik przechodzi do formularza z wypełnionymi danymi, które po
zmianie i zatwierdzeniu aktualizują wpis w SQLite. System ma potwierdzić
edycję komunikatem Toast.
• Usuwanie produktu (AlertDialog)
Każdy produkt powinien mieć przycisk „Usuń”. Po jego kliknięciu wyświetlany
jest AlertDialog z potwierdzeniem operacji. Po potwierdzeniu produkt zostaje
usunięty z bazy danych i listy.
• Automatyczne odświeżanie listy
Po każdej edycji lub usunięciu produktu lista musi automatycznie się
zaktualizować bez ponownego uruchamiania aplikacji.
• Walidacja formularza
Wprowadzić kontrolę danych: żadne pole nie może być puste, cena musi być
większa od 0, a data ważności nie może być wcześniejsza niż dzień bieżący. W
przypadku błędnych danych formularz nie może się zapisać i powinien
wyświetlić komunikat błędu.

Dodatkowe I:
• Duplikacja produktu
dodać przycisk „Duplikuj”, który tworzy nowy produkt z identycznymi danymi jak
wybrany, lecz nowym ID.
• Oznaczanie produktu jako „zużyty”
checkbox przy produkcie, który oznacza, że został już użyty. Produkt zmienia
kolor na szary.
• Filtrowanie listy po statusie
możliwość przefiltrowania listy na: wszystkie / aktywne / zużyte.
• Dźwięk zapisu
po dodaniu lub edycji produktu odtworzyć krótki dźwięk potwierdzenia.

Dodatkowe II:
• Eksport listy produktów do CSV
zapisać wszystkie produkty w pliku CSV w pamięci urządzenia.
• Import produktów z CSV
umożliwić wczytanie pliku CSV i zapisanie jego zawartości do SQLite.
• Eksport listy do JSON
analogicznie, eksport danych do pliku JSON.
• Import listy z JSON
wczytanie pliku JSON i dodanie jego danych do bazy.

Sprint 3: Moduł kaucji i skaner kodów
Obowiązkowe:
• Formularz dodawania opakowania
Utworzyć ekran z polami: typ opakowania, wartość kaucji (zł), kod kreskowy. Po
wprowadzeniu danych i zapisaniu wpis trafia do SQLite.
• Lista opakowań z sumą wartości kaucji
Wyświetlić wszystkie zapisane opakowania w RecyclerView i w stopce listy
pokazać sumę łącznej wartości kaucji. Dane mają się aktualizować po każdej
zmianie w bazie.
• Edycja i usuwanie opakowań
Dodać analogicznie jak w produktach możliwość edycji oraz trwałego usuwania
opakowań.
• Skaner kodów kreskowych
po kliknięciu przycisku „Skanuj” aparat skanuje kod kreskowy i automatycznie
uzupełnia pole kodu w formularzu.

Dodatkowe I:
• Sortowanie po wartości kaucji
dodanie możliwości sortowania listy od najniższej do najwyższej kaucji.
• Filtrowanie po typie opakowania
np. butelki, puszki, inne.
• Animacja slide-in przy dodaniu
nowy element pojawia się z animacją przesunięcia z dołu.
• Oznaczenie jako „zwrócone”
przy każdym opakowaniu checkbox, po zaznaczeniu zmienia kolor na zielony i
nie wlicza się do sumy aktywnych kaucji.

Dodatkowe II:
• Eksport opakowań do CSV 
zapis listy opakowań do pliku CSV.
• Eksport opakowań do PDF
stworzenie pliku PDF z listą opakowań i ich wartościami.
• Podsumowanie miesięczne liczby oddanych opakowań [NOT DONE]
obliczenie i wyświetlenie w formie tekstowej w danym miesiącu.
• Symulacja punktów zwrotu na mapie [NOT DONE]
prosta mapa (Google Maps API) z przykładowymi lokalizacjami automatów do
zwrotu.

Sprint 4: Raporty i analizy

Obowiązkowe:
• Obliczanie sumy wydatków miesięcznie aplikacja ma sumować ceny wszystkich produktów dodanych w danym miesiącu.
• Obliczanie sumy odzyskanej kaucji miesięcznie analogicznie obliczać sumę wartości opakowań oznaczonych jako „zwrócone”.
• Generowanie wykresu słupkowego wykres pokazujący porównanie wydatków i odzyskanych kaucji
• Eksport raportu do CSV możliwość zapisania wyników raportu do pliku CSV.

Dodatkowe I:
• Raport wydatków według kategorii obliczenie sum dla każdej kategorii produktu.
• Wybór okresu raportowania użytkownik wybiera przedział dat i aplikacja oblicza dane tylko z tego okresu.
• Średnia cena produktu obliczenie średniej wartości produktów w danym miesiącu.
• Raport liczby przeterminowanych produktów zliczanie produktów, których termin minął.

Dodatkowe II:
• Raport PDF z podsumowaniem generowanie estetycznego raportu PDF z wykresami. [NOT DONE]
• Wykres kołowy udziału kategorii pokazuje procentowy udział kategorii w wydatkach.
• Eksport raportu e-mailem wysyłanie raportu PDF/CSV na adres e-mail. [NOT DONE]
• Raport w HTML zapis raportu w pliku HTML z prostą stylizacją.


Sprint 5: Multimedia edukacyjne
Obowiązkowe:
• Ekran „Edukacja” z listą materiałów (JSON)
Utworzyć ekran z listą filmów edukacyjnych (np. o ekologii, recyklingu, zdrowym
stylu życia) wczytanych z pliku JSON (zawierającego tytuł, opis, URL filmu).
• Odtwarzanie wideo w ExoPlayer
Po kliknięciu na pozycję z listy otwiera się odtwarzacz wideo z wykorzystaniem
ExoPlayer, wczytujący dany link.
• Kontrolki odtwarzania
Dodać przyciski Play, Pause, SeekBar do przewijania.
• Obsługa trybu pełnoekranowego
Umożliwić przełączenie widoku odtwarzacza w pełny ekran w orientacji
poziomej.

Dodatkowe I:
• Miniatury w liście materiałów
obok tytułów filmów dodać miniatury (thumbnail z YouTube API lub z linku).
• Dodawanie własnego linku
formularz pozwalający użytkownikowi dodać własny materiał wideo do listy
(zapis do SQLite).
• Historia oglądania
zapisywanie w SQLite daty ostatniego odtworzenia każdego filmu.
• Obsługa napisów .srt [NOT DONE]
umożliwić wczytanie i wyświetlanie napisów w formacie SRT.

Dodatkowe II:
• Tryb audio-only
możliwość słuchania wideo bez obrazu (np. podcasty).
• Playlista materiałów [NOT DONE]
użytkownik może dodać kilka filmów do kolejki odtwarzania.
• Powiadomienie systemowe z kontrolkami [NOT DONE]
w trakcie odtwarzania w tle pojawia się powiadomienie z przyciskami
sterowania. 
• Sleep Timer (automatyczne zatrzymanie odtwarzania)
Dodaj możliwość ustawienia czasu (np. 10, 20, 30 min), po którym odtwarzanie
automatycznie się zatrzyma
