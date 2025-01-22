<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdCoche']) and isset($_POST['Marca']) and isset($_POST['Modelo']) and isset($_POST['Plazas']) and isset($_POST['Puertas']) and isset($_POST['Transmision']) and isset($_POST['Combustible']) and isset($_POST['AireAcondicionado']) and isset($_POST['Bluetooth']) and isset($_POST['GPS']) and isset($_POST['Descripcion']) and isset($_POST['NombreFotoPrincipal']) and isset($_POST['FotoPrincipal'])){
                        $bbdd = new DBOperations();

                        if($bbdd->modificarDatosCoche($_POST['IdCoche'],$_POST['Marca'],$_POST['Modelo'],$_POST['Plazas'],$_POST['Puertas'],$_POST['Transmision'],$_POST['Combustible'],$_POST['AireAcondicionado'],$_POST['Bluetooth'],$_POST['GPS'],$_POST['Descripcion'],$_POST['NombreFotoPrincipal'],$_POST['FotoPrincipal'])){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = "Datos actualizados correctamente.";
                        }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
