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

  

# MAIN TASKS

---

## Opravit zobrazení stránky z emailu
## Celkové zabezpečení
## Hlídání chyb
## Authentication tokeny

# Workflow

- **Zákazník**
  - přihlásí se ✅
  - vytvoří rezervaci ✅
  - přijde mu email o vytvoření rezervace ✅

- **Admin**
  - přijde mu email o vytvoření nové rezervace (**STATUS:** `PENDING`) ✅
  
  - **podívá se na rezervaci, možnosti:**
    - **zamítnout** (**STATUS:** `CANCELED`) ✅
      - kliknutí na tlačítko Reject
      - smazání rezervace ✅
      - zákazníkovi přijde email o zamítnutí rezervace ✅

    - **schválit** (**STATUS:** `APPROVED`) ✅
      - kliknutí na tlačítko Approve
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