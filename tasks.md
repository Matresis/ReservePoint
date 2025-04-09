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


- **kliknutí na rezervaci**: ✅
    - zobrazení určité rezervace ✅


- **manipulace**: ✅
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


- **po úspěšné rezervaci** ✅
    - poslání emailu potvrzujícího rezervaci na zadanou adresu uživatele ✅
    - poslání emailu na adresu admina ✅


## Authentication tokeny

# MAIN TASKS

## Pro APPROVE admin nastaví STATUS ručně (povinnost vyplnění OrderTime) -> zaslání emailu o schválení
## Automatické přidání rezervace do kalendáře po schválení requestu pro potvrzení 
## Hlídat datumy a časy v kalendáři aby nebylo možné schválit dvě rezervace na jeden čas
## Při klikání usera na tlačítka pro request modifikací nebo potvrzení, napsat pod button důvod proč to nelze (rezervace není APPROVED / CONFIRMED)
## Opravit zobrazení stránky z emailu
## Stylování

---

## Celkové zabezpečení
## Hlídání chyb


# Workflow

- **Zákazník**
  - přihlásí se ✅
  - vytvoří rezervaci ✅
  - přijde mu email o vytvoření rezervace ✅

- **Admin**
  - přijde mu email o vytvoření nové rezervace (**STATUS:** `PENDING`) ✅
  
  - **podívá se na rezervaci, možnosti:**
    - **zamítnout** (**STATUS:** `CANCELED`) ✅
      - smazání rezervace ✅
      - zákazníkovi přijde email o zamítnutí rezervace ✅

    - **schválit** (**STATUS:** `APPROVED`) ✅
      - zákazníkovi přijde email o schválení rezervace spolu s návrhem data a času rezervace ✅

- **Zákazník**
  - podívá se se na rezervaci 
  - **pokud rezervace vyhovuje**
    - pošle request pro potvrzení rezervace

  - **pokud nevyhovuje**
    - vyplní formulář pro modifikace
    - pošlě request pro modifikaci

  - **pokud nevyhovuje**
    - vyplní formulář pro zrušení
    - pošle request pro zrušení

- **Admin**
  - **admin potvrdí request pro potvrzení** (**STATUS:** `CONFIRMED`) ✅
    - rezervace se přidá do kalendáře
    - zákazníkovi přijde email o potvrzení ✅

  - **admin zamítne request pro potvrzení** (**STATUS:** `APPROVED`)
    - rezervace zůstane bez změn
    - zákazníkovi přijde email o zamítnutí potvrzení s důvodem

  - **admin potvrdí request pro modifikaci** (**STATUS:** `APPROVED`)
    - změna údajů podle requestu
    - zákazníkovi přijde email o změně
  
  - **admin zamítne request pro modifikaci** (**STATUS:** `APPROVED` / `CONFIRMED`)
    - údaje se nezmění
    - zákazníkovi přijde email o zamítnutí změn
    - pokud je **STATUS:** `APPROVED` -> `APPROVED`
    - pokud je **STATUS:** `CONFIRMED` -> `APPROVED`
    - ... další žádání o modifikace do doby **potvrzení** / **zamítnutí**

  - **admin potvrdí request pro zrušení** (**STATUS:** `CANCELED`)
    - rezervace se vymaže z databáze
    - zákazníkovi přijde email o smazání rezervace na jeho request a důvodem