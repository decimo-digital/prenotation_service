# Prenotation_Service

Servizio che gestisce la prenotazione di un utente presso un locale

# ENV

E' possibile speciicare alcune variabili d'ambiente

|         Nome         | Descrizione                                        | Obbligatorio |            Default            |
| :------------------: | :------------------------------------------------- | :----------: | :---------------------------: |
|         PORT         | Specifica la porta sulla quale il servizio ascolta |              |             8080              |
|       DB_NAME        | Il nome del database da utilizzare                 |      x       |                               |
|       DB_TYPE        | Il tipo di connettore jdbc da utilizzare           |              |          postgresql           |
|       DB_HOST        | L'url del database                                 |      x       |                               |
|       DB_PORT        | La porta del DB                                    |              |             5432              |
|     DB_USERNAME      | L'utente da utilizzare per accedere al DB          |              |             admin             |
|     DB_PASSWORD      | La password per accedere al DB                     |              |          ceposto2021          |
| MERCHANT_SERVICE_URL | L'url del `merchant_service`                       |      x       | https://merchant_service:8080 |
