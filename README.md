# BiometricAuth

Демо-приложение демонстрирующее возможности биометрической аутентификации в Android приложении.
Для интеграции биометрии используется модуль <a href="https://github.com/NoDopezzz/BiometricAuth/tree/master/biomitric_auth">biometric_auth</a>.

<h4>Возможности библиотеки bimetric_auth:</h4>
<ul>
<li>Создание BiometricStoreManager</li>
<pre>
BiometricStoreManager.create()
</pre>
<li>Проверка возможности использования биометрии</li>
<pre>
BiometricStoreManager.isBiometricAvailable(context, biometricType)
</pre>
<li><code>BiometricType</code> - тип проверяемой биометрии.</li><br>
<p>
<code>FIRST_CLASS</code> используется для шифрования данных с помощью биометрии и требует более надежный способ подтверждения личности.<br>
<code>SECOND_CLASS</code> используется для простой аутентификации.<br>
</p>
<li>Простая аутентификация пользователя</li>
<pre>
BiometricStoreManager.authenticate(activity, onSuccess, onFailed)
</pre>
<li>Шифрование текста с помощью биометрии</li>
<pre>
BiometricStoreManager.encryptWithBiometric(activity, text, onSuccess, onFailed)
</pre>
<li>Расшифровка ранее сохраненного текста с помощью биометрии</li>
<pre>BiometricStoreManager.decryptWithBiometric(activity, onSuccess, onFailed)</pre>
</ul>
