## Registrace ✅
- jméno ✅
- přijmení ✅
- email ✅
- heslo ✅
## Přihlašování
- email / heslo ✅
- přihlášení admina ✅
- přihlášení usera ✅
- automatické (tokeny)
## Admin
- zobrazení seznamu rezervací ✅
- filtrování rezervací ✅
- #### kliknutí na rezervaci:*✅
    - zobrazení určité rezervace ✅
- #### manipulace: ✅
    - úprava ✅
    - odstranění ✅
    - nastavit čas ✅
    - poznámky ✅
    - přidat do kalendáře ✅
    - zobrazení kalendáře ✅
    - poslat email ✅
## User
- rezervování ✅
- po přihlášení možnost zobrazení rezervace pouze přihlášeného usera ✅
- filtrování rezervací ✅
- zobrazení určité rezervace ✅
- #### po úspěšné rezervaci ✅
    - poslání emailu potvrzujícího rezervaci na zadanou adresu uživatele ✅
    - poslání emailu na adresu admina ✅

# Manipulace s rezervací

- po vytvoření rezervace: ✅
    - **STATUS:** `PENDING` ✅
    - **OrderTime:** `null` ✅
    - Zaslání emailu zákazníkovi o přijetí žádosti o rezervaci ✅
    - Zaslání emailu správci o nové žádosti ✅


- pro schválení rezervace:
    - Nastavení **OrderTime**
    - Nastavení **STATUS** na `APPROVED`
    - Zaslání emailu zákazníkovi o schválení rezervace společně s návrhem **OrderTime**


- pro potvrzení rezervace:
    - Nastavení finálního **OrderTime**
    - Přidání rezervace do kalendáře
    - Nastavení **STATUS** automaticky na `CONFIRMED`
    - Zaslání emailu zákazníkovi o potvrzení a rekapitulací rezervace


- pro zamítnutí rezervace
    - Nastavení **STATUS** na `CANCELED`
    - Vyplnění důvodu v **Notes**
    - Automatické smazání rezervaci z databáze
    - Zaslání emailu zákazníkovi o zamítnutí rezervace


- zrušení rezervace po potvrzení
    - Vyplnění důvodu v **Notes**
    - Vymazání rezervace
    - Zaslání emailu zákazníkovi o zrušení rezervace

# Stylování
# Celkové zabezpečení
# Hlídání chyb
# Authentication tokeny