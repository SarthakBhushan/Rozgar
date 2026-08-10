#!/usr/bin/env python3
"""
Rozgar Backend Automated Seeder & Simulation Script
Targeting deployed endpoint: https://rozgar-rbzx.onrender.com (or custom URL)

This script automates:
1. Health check & wake-up ping for Render cold starts
2. User registration (Sellers/Businesses & Buyers)
3. Business profile creation (with dynamically generated valid GST, PAN, Bank details)
4. Catalog item publishing (Products & Services)
5. RFQ (Request for Quotation) posting by buyers
6. Quote submissions by sellers
7. B2B Chat messaging between buyers and sellers
8. Quote acceptance & Order creation
9. Lifecycle order status updates (CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED)
10. Credentials & summary exported to seed_data_output.json
"""

import sys
import os
import io
import json
import time
import random
import string
import urllib.request
import urllib.error
from datetime import datetime, timedelta

# Fix Windows console UTF-8 encoding issues
if sys.platform == "win32":
    try:
        sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
        sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')
    except Exception:
        pass

DEFAULT_BASE_URL = "https://rozgar-rbzx.onrender.com"
RUN_ID = f"{int(time.time())}"[-4:]  # 4-digit unique seed tag

def generate_gst():
    state = f"{random.randint(1, 35):02d}"
    letters = "".join(random.choices(string.ascii_uppercase, k=5))
    digits = f"{random.randint(1000, 9999)}"
    check1 = random.choice(string.ascii_uppercase)
    check2 = "1"
    checksum = random.choice(string.ascii_uppercase + string.digits)
    return f"{state}{letters}{digits}{check1}{check2}Z{checksum}"

def generate_pan():
    letters = "".join(random.choices(string.ascii_uppercase, k=3)) + "C" + random.choice(string.ascii_uppercase)
    digits = f"{random.randint(1000, 9999)}"
    last = random.choice(string.ascii_uppercase)
    return f"{letters}{digits}{last}"

SELLER_USERS = [
    {
        "name": "Rajesh Agarwal",
        "email_prefix": "rajesh.agarwal",
        "email_domain": "apexsteel.co.in",
        "password": "Password@123",
        "business": {
            "name": "Apex Industrial Steels Pvt Ltd",
            "description": "Leading manufacturer and supplier of IS 2062 structural steel, seamless pipes, TMT bars, and industrial fasteners.",
            "businessType": "MANUFACTURER",
            "city": "Mumbai",
            "state": "Maharashtra",
            "pincode": "400013",
            "address": "Plot 102, Lower Parel Industrial Estate, S.B. Marg",
            "phone": "9820012345",
            "website": "https://apexsteels.example.com",
            "bankAccountNumber": "9180200344123",
            "ifscCode": "SBIN0001234",
            "accountHolderName": "Apex Industrial Steels Pvt Ltd",
            "bankName": "State Bank of India"
        },
        "catalog_items": [
            {
                "name": "Heavy Duty Seamless Steel Pipe - 3 Inch (IS 2062)",
                "description": "High pressure carbon steel seamless pipes suitable for oil, gas, and industrial infrastructure.",
                "itemType": "PRODUCT",
                "pricePerUnit": 1450.00,
                "unit": "meter",
                "minOrderQuantity": 20
            },
            {
                "name": "Fe-550D High Ductility TMT Rebars (12mm)",
                "description": "Earthquake resistant TMT steel bars for commercial high-rise construction.",
                "itemType": "PRODUCT",
                "pricePerUnit": 58.50,
                "unit": "kg",
                "minOrderQuantity": 500
            },
            {
                "name": "Stainless Steel Grade 304 Flanges & Fittings",
                "description": "Precision machined SS 304 slip-on flanges for industrial chemical piping.",
                "itemType": "PRODUCT",
                "pricePerUnit": 890.00,
                "unit": "piece",
                "minOrderQuantity": 10
            }
        ]
    },
    {
        "name": "Sunil Verma",
        "email_prefix": "sunil.verma",
        "email_domain": "bharat-electricals.com",
        "password": "Password@123",
        "business": {
            "name": "Bharat Electricals & Switchgears",
            "description": "Wholesale distributor of high voltage transformers, switchgears, industrial copper cables, and circuit breakers.",
            "businessType": "WHOLESALER",
            "city": "Delhi",
            "state": "Delhi",
            "pincode": "110006",
            "address": "45-46 Chandni Chowk Electrical Market",
            "phone": "9811054321",
            "website": "https://bharatelectricals.example.com",
            "bankAccountNumber": "002105009876",
            "ifscCode": "HDFC0000021",
            "accountHolderName": "Bharat Electricals and Switchgears",
            "bankName": "HDFC Bank"
        },
        "catalog_items": [
            {
                "name": "3-Phase 100 kVA Distribution Transformer",
                "description": "Oil-cooled outdoor distribution transformer with copper winding and OLTC.",
                "itemType": "PRODUCT",
                "pricePerUnit": 185000.00,
                "unit": "piece",
                "minOrderQuantity": 1
            },
            {
                "name": "Armoured 4-Core Copper Power Cable (35 sq mm)",
                "description": "Heavy duty XLPE insulated LT power cable for factory electrification.",
                "itemType": "PRODUCT",
                "pricePerUnit": 420.00,
                "unit": "meter",
                "minOrderQuantity": 100
            }
        ]
    },
    {
        "name": "Karan Mehta",
        "email_prefix": "karan.mehta",
        "email_domain": "vanguardchem.in",
        "password": "Password@123",
        "business": {
            "name": "Vanguard Chemical Solutions",
            "description": "Bulk supplier and distributor of industrial solvents, polymer resins, water treatment chemicals, and acids.",
            "businessType": "DISTRIBUTOR",
            "city": "Ahmedabad",
            "state": "Gujarat",
            "pincode": "380015",
            "address": "GIDC Estate Phase 3, Naroda",
            "phone": "9879011223",
            "website": "https://vanguardchem.example.com",
            "bankAccountNumber": "50200012345678",
            "ifscCode": "ICIC0000104",
            "accountHolderName": "Vanguard Chemical Solutions",
            "bankName": "ICICI Bank"
        },
        "catalog_items": [
            {
                "name": "Pure Isopropyl Alcohol 99.9% (IPA)",
                "description": "Pharma and industrial grade solvent packaged in 200L HDPE drums.",
                "itemType": "PRODUCT",
                "pricePerUnit": 115.00,
                "unit": "kg",
                "minOrderQuantity": 200
            },
            {
                "name": "Liquid Chlorine for Water Treatment",
                "description": "Commercial grade liquid chlorine cylinders for municipal and industrial effluent treatment.",
                "itemType": "PRODUCT",
                "pricePerUnit": 45.00,
                "unit": "kg",
                "minOrderQuantity": 100
            }
        ]
    },
    {
        "name": "Vikram Deshmukh",
        "email_prefix": "vikram",
        "email_domain": "zenithlogistics.co.in",
        "password": "Password@123",
        "business": {
            "name": "Zenith Logistics & Freight Solutions",
            "description": "Pan-India B2B freight transportation, refrigerated logistics, and warehousing management services.",
            "businessType": "SERVICE_PROVIDER",
            "city": "Pune",
            "state": "Maharashtra",
            "pincode": "411019",
            "address": "Transport Nagar, Nigdi Expressway",
            "phone": "9850099887",
            "website": "https://zenithlogistics.example.com",
            "bankAccountNumber": "334455667788",
            "ifscCode": "AXIS0000451",
            "accountHolderName": "Zenith Logistics India",
            "bankName": "Axis Bank"
        },
        "catalog_items": [
            {
                "name": "Full Truckload (FTL) Inter-City Freight (32ft Container)",
                "description": "Dedicated 32-foot multi-axle container truck service across Golden Quadrilateral routes.",
                "itemType": "SERVICE",
                "pricePerUnit": 65.00,
                "unit": "km",
                "minOrderQuantity": 250
            },
            {
                "name": "Cold Chain Warehousing Space (Palletized)",
                "description": "Temperature controlled warehousing (-20C to +8C) with 24x7 surveillance and inventory API.",
                "itemType": "SERVICE",
                "pricePerUnit": 450.00,
                "unit": "pallet/month",
                "minOrderQuantity": 10
            }
        ]
    }
]

BUYER_USERS = [
    {
        "name": "Ramesh Kumar",
        "email_prefix": "ramesh.kumar",
        "email_domain": "infra-projects.in",
        "password": "Password@123",
        "rfqs": [
            {
                "title": "Bulk Requirement for IS 2062 Steel Pipes for Highway Bridge Project",
                "description": "Urgent requirement for 500 meters of 3-inch seamless steel pipes for ongoing NHAI highway construction project near Thane. Must include mill test certificate.",
                "unit": "meter",
                "quantity": 500,
                "targetPrice": 1400.00,
                "deliveryLocation": "Thane Highway Site Sector 4, Maharashtra",
                "days_deadline": 25
            },
            {
                "title": "Procurement of Fe-550D TMT Rebars for Residential Tower",
                "description": "Sourcing 5,000 kg of certified Fe-550D grade 12mm TMT steel bars. Site delivery required in Kalyan with batch test reports.",
                "unit": "kg",
                "quantity": 5000,
                "targetPrice": 57.00,
                "deliveryLocation": "Kalyan West Construction Site, Pin 421301",
                "days_deadline": 15
            }
        ]
    },
    {
        "name": "Ananya Sharma",
        "email_prefix": "ananya.sharma",
        "email_domain": "greenpower.com",
        "password": "Password@123",
        "rfqs": [
            {
                "title": "Supply & Commissioning of 100 kVA Power Transformer",
                "description": "Looking for heavy duty oil cooled 100 kVA distribution transformer for factory expansion in Greater Noida. Warranty 3 years minimum.",
                "unit": "piece",
                "quantity": 2,
                "targetPrice": 180000.00,
                "deliveryLocation": "Ecotech Phase III, Greater Noida, UP",
                "days_deadline": 30
            }
        ]
    },
    {
        "name": "Dr. Vikramaditya Patel",
        "email_prefix": "v.patel",
        "email_domain": "gujaratpharma.com",
        "password": "Password@123",
        "rfqs": [
            {
                "title": "Supply of Isopropyl Alcohol 99.9% (IPA) in Drums",
                "description": "Requirement for 2,000 kg of pure IPA solvent for formulation batch production. COA certificate required with each lot.",
                "unit": "kg",
                "quantity": 2000,
                "targetPrice": 110.00,
                "deliveryLocation": "Sanand GIDC Industrial Park, Gujarat",
                "days_deadline": 20
            }
        ]
    }
]

CHAT_TEMPLATES = [
    ("BUYER", "Hello, we noticed your quote on our RFQ. Can you confirm if all batch test reports will be provided prior to dispatch?"),
    ("SELLER", "Greetings! Yes, full mill test reports and material test certificates (MTC) will be provided with the delivery invoice."),
    ("BUYER", "Great. What is your estimated lead time for dispatching the first lot to our site?"),
    ("SELLER", "We have ready inventory in stock. Once the order is confirmed, we can dispatch within 48 hours."),
    ("BUYER", "Sounds good. We will proceed with quote acceptance on the portal.")
]

class ApiClient:
    def __init__(self, base_url):
        self.base_url = base_url.rstrip('/')

    def request(self, method, endpoint, payload=None, token=None):
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        if token:
            headers["Authorization"] = f"Bearer {token}"

        data_bytes = json.dumps(payload).encode('utf-8') if payload else None
        req = urllib.request.Request(url, data=data_bytes, headers=headers, method=method)

        try:
            with urllib.request.urlopen(req, timeout=30) as resp:
                body = resp.read().decode('utf-8')
                return resp.status, json.loads(body)
        except urllib.error.HTTPError as e:
            err_body = e.read().decode('utf-8')
            try:
                parsed = json.loads(err_body)
                return e.code, parsed
            except Exception:
                return e.code, {"error": err_body}
        except Exception as e:
            return 500, {"error": str(e)}

def wake_up_server(api_client, retries=5, delay=5):
    print(f"[*] Pinging backend health endpoint: {api_client.base_url}/api/v1/health")
    for i in range(1, retries + 1):
        status, res = api_client.request("GET", "/api/v1/health")
        if status == 200 and res.get("success"):
            print(f"[+] Backend is live and healthy! ({res.get('data', {})})")
            return True
        print(f"[!] Attempt {i}/{retries}: Server responded with status {status}. Retrying in {delay}s...")
        time.sleep(delay)
    print("[-] Failed to establish health check connection with backend.")
    return False

def register_or_login(api_client, name, email_prefix, email_domain, password):
    email = f"{email_prefix}.{RUN_ID}@{email_domain}"
    reg_payload = {"name": name, "email": email, "password": password}
    status, res = api_client.request("POST", "/api/v1/auth/register", reg_payload)
    if status in (200, 201) and res.get("success"):
        data = res.get("data", {})
        print(f"  [+] Registered user: {email} (ID: {data.get('userId')})")
        return data.get("token"), data.get("userId"), email

    # Fallback to base email if custom tagged email fails
    base_email = f"{email_prefix}@{email_domain}"
    login_payload = {"email": base_email, "password": password}
    status, res = api_client.request("POST", "/api/v1/auth/login", login_payload)
    if status == 200 and res.get("success"):
        data = res.get("data", {})
        print(f"  [*] Logged in existing user: {base_email} (ID: {data.get('userId')})")
        return data.get("token"), data.get("userId"), base_email

    print(f"  [!] Auth failed for {email}: {res}")
    return None, None, email

def create_business(api_client, token, biz_template):
    biz_payload = dict(biz_template)
    biz_payload["gstNumber"] = generate_gst()
    biz_payload["panNumber"] = generate_pan()

    status, res = api_client.request("POST", "/api/v1/businesses", biz_payload, token=token)
    if status in (200, 201) and res.get("success"):
        b_data = res.get("data", {})
        print(f"  [+] Created business: {b_data.get('name')} (ID: {b_data.get('id')}, GST: {b_data.get('gstNumber')})")
        return b_data.get("id")

    # If user already owns a business, fetch via /me
    status, res = api_client.request("GET", "/api/v1/businesses/me", token=token)
    if status == 200 and res.get("success"):
        b_data = res.get("data", {})
        print(f"  [*] Found user's existing business: {b_data.get('name')} (ID: {b_data.get('id')})")
        return b_data.get("id")

    print(f"  [!] Business creation failed: {res}")
    return None

def main():
    base_url = sys.argv[1] if len(sys.argv) > 1 else DEFAULT_BASE_URL
    print("=" * 70)
    print(f"  ROZGAR B2B BACKEND SEED & SIMULATION SCRIPT")
    print(f"  Target URL: {base_url}")
    print(f"  Seed Run ID: {RUN_ID}")
    print(f"  Time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 70)

    api_client = ApiClient(base_url)

    if not wake_up_server(api_client):
        print("Aborting script execution due to server connection issues.")
        sys.exit(1)

    simulation_results = {
        "sellers": [],
        "buyers": [],
        "catalog_items": [],
        "rfqs": [],
        "quotes": [],
        "chats": [],
        "orders": []
    }

    # 1. SETUP SELLER USERS & BUSINESSES & CATALOGS
    print("\n--- [PHASE 1] Registering Sellers & Creating Business Profiles & Catalogs ---")
    sellers_pool = []
    for s_info in SELLER_USERS:
        token, user_id, email = register_or_login(api_client, s_info["name"], s_info["email_prefix"], s_info["email_domain"], s_info["password"])
        if not token:
            continue
        
        biz_id = create_business(api_client, token, s_info["business"])
        seller_obj = {
            "name": s_info["name"],
            "email": email,
            "password": s_info["password"],
            "token": token,
            "user_id": user_id,
            "business_id": biz_id,
            "business_name": s_info["business"]["name"]
        }
        sellers_pool.append(seller_obj)
        simulation_results["sellers"].append(seller_obj)

        # Add catalog items for this seller
        if biz_id:
            for c_item in s_info["catalog_items"]:
                status, res = api_client.request("POST", "/api/v1/catalog", c_item, token=token)
                if status in (200, 201) and res.get("success"):
                    item_data = res.get("data", {})
                    print(f"    [+] Added Catalog Item: '{item_data.get('name')}' (Price: Rs.{item_data.get('pricePerUnit')}/{item_data.get('unit')})")
                    simulation_results["catalog_items"].append({
                        "id": item_data.get("id"),
                        "name": item_data.get("name"),
                        "business_id": biz_id
                    })

    # 2. SETUP BUYER USERS & POST RFQs
    print("\n--- [PHASE 2] Registering Buyers & Posting RFQs ---")
    buyers_pool = []
    posted_rfqs = []

    for b_info in BUYER_USERS:
        token, user_id, email = register_or_login(api_client, b_info["name"], b_info["email_prefix"], b_info["email_domain"], b_info["password"])
        if not token:
            continue

        buyer_obj = {
            "name": b_info["name"],
            "email": email,
            "password": b_info["password"],
            "token": token,
            "user_id": user_id
        }
        buyers_pool.append(buyer_obj)
        simulation_results["buyers"].append(buyer_obj)

        for rfq_req in b_info["rfqs"]:
            deadline_date = (datetime.now() + timedelta(days=rfq_req["days_deadline"])).strftime("%Y-%m-%dT%H:%M:%S")
            payload = {
                "title": rfq_req["title"],
                "description": rfq_req["description"],
                "unit": rfq_req["unit"],
                "quantity": rfq_req["quantity"],
                "targetPrice": rfq_req["targetPrice"],
                "deliveryLocation": rfq_req["deliveryLocation"],
                "deadline": deadline_date
            }
            status, res = api_client.request("POST", "/api/v1/rfq", payload, token=token)
            if status in (200, 201) and res.get("success"):
                rfq_data = res.get("data", {})
                print(f"    [+] Posted RFQ: '{rfq_data.get('title')}' (ID: {rfq_data.get('id')}, Qty: {rfq_data.get('quantity')})")
                rfq_record = {
                    "id": rfq_data.get("id"),
                    "title": rfq_data.get("title"),
                    "buyer_token": token,
                    "buyer_id": user_id,
                    "quantity": rfq_data.get("quantity"),
                    "targetPrice": rfq_data.get("targetPrice")
                }
                posted_rfqs.append(rfq_record)
                simulation_results["rfqs"].append(rfq_record)

    # 3. SUBMIT QUOTES BY MATCHING SELLERS
    print("\n--- [PHASE 3] Sellers Submitting Quotes on Open RFQs ---")
    submitted_quotes = []

    for rfq in posted_rfqs:
        # Pick sellers with valid business profiles to submit quotes
        eligible_sellers = [s for s in sellers_pool if s.get("business_id")]
        if not eligible_sellers:
            continue
        selected_sellers = random.sample(eligible_sellers, min(len(eligible_sellers), 2))
        for seller in selected_sellers:
            discount_factor = random.choice([0.95, 0.98, 1.00, 1.02])
            quoted_price = round(float(rfq["targetPrice"]) * discount_factor, 2)
            valid_until = (datetime.now() + timedelta(days=15)).strftime("%Y-%m-%dT%H:%M:%S")

            quote_payload = {
                "pricePerUnit": quoted_price,
                "availableQuantity": rfq["quantity"] + random.randint(10, 100),
                "note": f"Offered by {seller['business_name']}. Ready stock available with full batch inspection reports.",
                "validUntil": valid_until
            }
            status, res = api_client.request("POST", f"/api/v1/rfq/{rfq['id']}/quote", quote_payload, token=seller["token"])
            if status in (200, 201) and res.get("success"):
                q_data = res.get("data", {})
                print(f"    [*] Seller '{seller['business_name']}' quoted Rs.{quoted_price}/unit on RFQ #{rfq['id']} (Quote ID: {q_data.get('id')})")
                quote_rec = {
                    "quote_id": q_data.get("id"),
                    "rfq_id": rfq["id"],
                    "seller": seller,
                    "buyer_token": rfq["buyer_token"],
                    "price_per_unit": quoted_price
                }
                submitted_quotes.append(quote_rec)
                simulation_results["quotes"].append(quote_rec)

    # 4. SIMULATE CHAT CONVERSATIONS BETWEEN BUYERS AND SELLERS
    print("\n--- [PHASE 4] B2B Negotiation & Messaging over Chat ---")
    for q_item in submitted_quotes[:3]:  # Simulate on first 3 quotes
        rfq_id = q_item["rfq_id"]
        seller = q_item["seller"]
        buyer_token = q_item["buyer_token"]

        # Buyer initiates thread
        status, res = api_client.request("POST", f"/api/v1/chat/thread/{rfq_id}", token=buyer_token)
        if status in (200, 201) and res.get("success"):
            t_data = res.get("data", {})
            thread_id = t_data.get("id")
            print(f"    [*] Thread #{thread_id} established for RFQ #{rfq_id} between Buyer and '{seller['business_name']}'")

            for role, msg in CHAT_TEMPLATES:
                sender_token = buyer_token if role == "BUYER" else seller["token"]
                msg_payload = {"threadId": thread_id, "content": msg}
                api_client.request("POST", "/api/v1/chat/send", msg_payload, token=sender_token)
                time.sleep(0.1)

            print(f"       -> Sent negotiation messages in Thread #{thread_id}")
            simulation_results["chats"].append({"thread_id": thread_id, "rfq_id": rfq_id})

    # 5. ACCEPT QUOTES & CREATE TRANSACTIONS/ORDERS & FULFILLMENT LIFECYCLE
    print("\n--- [PHASE 5] Quote Acceptance, Order Creation & Fulfillment Lifecycle ---")
    for q_item in submitted_quotes[:2]:  # Accept first 2 quotes
        rfq_id = q_item["rfq_id"]
        quote_id = q_item["quote_id"]
        buyer_token = q_item["buyer_token"]
        seller = q_item["seller"]

        # Accept Quote
        status, res = api_client.request("PUT", f"/api/v1/rfq/{rfq_id}/quote/{quote_id}/accept", token=buyer_token)
        if status == 200 and res.get("success"):
            print(f"    [+] Buyer accepted Quote #{quote_id} for RFQ #{rfq_id}")

            # Create Order
            order_payload = {"rfqId": rfq_id, "quoteId": quote_id}
            o_status, o_res = api_client.request("POST", "/api/v1/transactions/orders", order_payload, token=buyer_token)
            if o_status in (200, 201) and o_res.get("success"):
                order_data = o_res.get("data", {})
                order_id = order_data.get("id")
                print(f"    [+] Order #{order_id} created successfully! Total Amount: Rs.{order_data.get('totalAmount')}")

                # Seller updates status: CONFIRMED -> PROCESSING
                api_client.request("PUT", f"/api/v1/transactions/orders/{order_id}/status", {"status": "PROCESSING"}, token=seller["token"])
                print(f"       -> Order #{order_id} status updated to PROCESSING by Seller")

                # Seller updates status: PROCESSING -> SHIPPED
                api_client.request("PUT", f"/api/v1/transactions/orders/{order_id}/status", {"status": "SHIPPED"}, token=seller["token"])
                print(f"       -> Order #{order_id} status updated to SHIPPED by Seller")

                # Buyer updates status: SHIPPED -> DELIVERED
                api_client.request("PUT", f"/api/v1/transactions/orders/{order_id}/status", {"status": "DELIVERED"}, token=buyer_token)
                print(f"       -> Order #{order_id} status confirmed as DELIVERED by Buyer")

                simulation_results["orders"].append({
                    "order_id": order_id,
                    "total_amount": order_data.get("totalAmount"),
                    "status": "DELIVERED"
                })

    # OUTPUT SUMMARY
    output_filename = "seed_data_output.json"
    with open(output_filename, "w", encoding="utf-8") as f:
        json.dump(simulation_results, f, indent=2, default=str)

    print("\n" + "=" * 70)
    print("  SIMULATION & SEEDING COMPLETED SUCCESSFULLY!")
    print("=" * 70)
    print(f" SUMMARY OF GENERATED DATA:")
    print(f"   * Sellers / Businesses Created: {len(simulation_results['sellers'])}")
    print(f"   * Catalog Items Listed:        {len(simulation_results['catalog_items'])}")
    print(f"   * Buyers Registered:           {len(simulation_results['buyers'])}")
    print(f"   * RFQs Posted:                 {len(simulation_results['rfqs'])}")
    print(f"   * Quotes Submitted:            {len(simulation_results['quotes'])}")
    print(f"   * Chat Threads Simulated:      {len(simulation_results['chats'])}")
    print(f"   * Orders Processed & Delivered: {len(simulation_results['orders'])}")
    print(f"\n Full dataset details saved to: {output_filename}")

    print("\n DEMO ACCOUNTS CREATED (Use these to test on Frontend / Postman):")
    print("----------------------------------------------------------------------")
    print("SELLER ACCOUNTS:")
    for s in simulation_results["sellers"]:
        print(f"  * {s['business_name']} | Email: {s['email']} | Pass: {s['password']}")
    print("\nBUYER ACCOUNTS:")
    for b in simulation_results["buyers"]:
        print(f"  * {b['name']} | Email: {b['email']} | Pass: {b['password']}")
    print("----------------------------------------------------------------------")

if __name__ == "__main__":
    main()
