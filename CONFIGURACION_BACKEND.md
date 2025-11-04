# 📱 Configuración de Conexión Android → Backend

## ⚙️ Configurar la IP del Backend

El archivo `RetrofitProvider.kt` está configurado para conectarse al backend. Necesitas ajustar la IP según tu escenario:

### 📍 Opciones de Configuración

Edita el archivo: `app/src/main/java/com/example/voltmarket/network/RetrofitProvider.kt`

```kotlin
private const val USE_EMULATOR = true  // ← Cambia esto según tu caso
private const val EMULATOR_IP = "10.0.2.2"
private const val PHYSICAL_IP = "192.168.100.24"  // ← Pon aquí tu IP real
```

### 📱 **Caso 1: Emulador de Android Studio**
```kotlin
private const val USE_EMULATOR = true
```
✅ Usa `10.0.2.2:8080` (el emulador redirige a localhost de tu PC)

### 📱 **Caso 2: Dispositivo Físico**
```kotlin
private const val USE_EMULATOR = false
private const val PHYSICAL_IP = "192.168.X.X"  // Tu IP local
```

#### ¿Cómo obtener tu IP?

**En Windows (PowerShell):**
```bash
ipconfig
```
Busca "IPv4" en la sección de tu adaptador WiFi/Ethernet

**En tu dispositivo Android:**
- Ambos dispositivos DEBEN estar en la misma red WiFi
- El backend debe estar corriendo en `http://TU_IP:8080`

---

## 🚀 Pasos para Probar la Aplicación

### 1️⃣ **Iniciar el Backend**

En tu proyecto backend (`backend-voltmarket`):

```bash
# Compilar
mvn clean install

# Iniciar
mvn spring-boot:run
```

Verifica que esté corriendo en: `http://localhost:8080`

### 2️⃣ **Cargar Datos Semilla (Opcional pero recomendado)**

```bash
# Desde la raíz del proyecto backend
mysql -u root -p voltmarket_db < src/main/resources/db/seed_products.sql
```

Esto cargará 50+ productos de ejemplo para probar la app.

### 3️⃣ **Verificar Backend desde Browser**

Abre en tu navegador:
- `http://localhost:8080/api/products` → Deberías ver JSON con productos
- `http://localhost:8080/api/products/categories` → Deberías ver las categorías

### 4️⃣ **Configurar Android**

1. Abre `RetrofitProvider.kt`
2. Ajusta `USE_EMULATOR` según tu caso
3. Si usas dispositivo físico, pon tu IP real en `PHYSICAL_IP`
4. Sincroniza el proyecto (Sync Now)

### 5️⃣ **Ejecutar la App Android**

1. Conecta tu dispositivo o inicia el emulador
2. Click en **Run** (▶️) en Android Studio
3. La app debería:
   - Mostrar la pantalla de login/registro
   - Permitir crear cuenta
   - Mostrar los productos del backend

---

## 🔍 Solución de Problemas

### ❌ Error: "Unable to resolve host" o "Failed to connect"

**Causa:** La app no puede conectarse al backend

**Soluciones:**
1. Verifica que el backend esté corriendo:
   ```bash
   curl http://localhost:8080/api/products
   ```

2. Si usas dispositivo físico:
   - Verifica que ambos estén en la misma WiFi
   - Verifica que Windows Firewall no bloquee el puerto 8080
   - Intenta acceder desde el navegador del móvil: `http://TU_IP:8080/api/products`

3. Si usas emulador:
   - Asegúrate de usar `10.0.2.2` no `localhost`
   - Reinicia el emulador

### ❌ Error: "Unexpected JSON token"

**Causa:** El backend devuelve HTML en lugar de JSON (probablemente una página de error)

**Soluciones:**
1. Revisa los logs del backend
2. Verifica que la URL sea correcta en `RetrofitProvider.kt`
3. Asegúrate que el endpoint existe: `GET /api/products`

### ❌ Error: "401 Unauthorized" en productos

**Causa:** El token JWT no se está enviando correctamente

**Soluciones:**
1. Asegúrate de hacer login primero
2. Verifica que `SharedPrefsManager` guarde el token
3. Revisa los logs de `HttpLoggingInterceptor` en Logcat

### ❌ Los productos no aparecen (pantalla vacía)

**Causas posibles:**
1. No hay productos en la base de datos
   - **Solución:** Carga los datos semilla (ver paso 2️⃣)
2. El backend devuelve lista vacía
   - **Solución:** Crea productos manualmente o ejecuta el SQL de seed
3. Error de parseo JSON
   - **Solución:** Revisa Logcat para ver el error exacto

---

## 📊 Verificar Logs

En Android Studio, abre **Logcat** y filtra por:
- `OkHttp` → Ver requests/responses HTTP
- `System.out` → Ver println de errores
- `voltmarket` → Ver logs de tu app

Busca líneas rojas (errores) y verifica:
1. ¿Se está haciendo el request correcto?
2. ¿Qué respuesta devuelve el backend?
3. ¿Hay errores de parseo JSON?

---

## ✅ Checklist de Configuración

Antes de presentar, verifica:

- [ ] Backend compila sin errores
- [ ] Backend está corriendo en puerto 8080
- [ ] Base de datos tiene productos (ejecutaste seed_products.sql)
- [ ] Android puede acceder a `http://IP:8080/api/products`
- [ ] RetrofitProvider tiene la IP correcta
- [ ] La app compila sin errores
- [ ] Puedes registrarte y hacer login
- [ ] Los productos se muestran en HomeScreen
- [ ] Puedes ver detalle de un producto
- [ ] Puedes crear un producto nuevo

---

## 🎯 Endpoints que Usa la App

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registro de usuario |
| POST | `/api/auth/login` | Login de usuario |
| GET | `/api/products` | Listar productos |
| GET | `/api/products/{id}` | Detalle de producto |
| POST | `/api/products` | Crear producto |
| PUT | `/api/products/{id}` | Actualizar producto |
| DELETE | `/api/products/{id}` | Eliminar producto |
| GET | `/api/products/categories` | Listar categorías |
| POST | `/api/favorites` | Agregar a favoritos |
| POST | `/api/likes` | Dar like a producto |

---

## 📞 Contacto para Soporte

Si tienes problemas, revisa:
1. **Logcat** en Android Studio (errores de la app)
2. **Console** en IntelliJ/Eclipse (errores del backend)
3. **Postman/Browser** para verificar que el backend responde

¡Buena suerte con la presentación! 🚀

